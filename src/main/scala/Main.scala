import scala.slick.driver.PostgresDriver
import destination.DepositsPlaces
import destination.Expositions
import destination.NewDepositsPlace
import destination.NewExposition
import destination.TargetDatabase
import source.LegacyCategories
import source.LegacyDatabase
import source.LegacyDepositsPlaces
import source.LegacyExposition
import source.LegacyExpositions
import source.LegacyHierarchicalEntity
import source.LegacyCategory
import destination.NewCategory
import destination.NewCategory
import destination.Categories
import source.LegacyTestimonials
import destination.Testimonials
import destination.Testimonial
import destination.NewTestimonial
import source.LegacyArticles
import destination.Images
import destination.NewImage
import destination.Articles
import destination.NewArticle
import destination.SpecimenPhotos
import destination.Specimens
import source.LegacySpecimens
import destination.NewDepositsPlace
import source.LegacyDepositsPlace
import destination.NewSpecimen
import source.LegacySpecimenPhotos
import destination.NewSpecimenPhoto

object Main extends App with Logging {
  def insertHierarchy[TLegacy <: LegacyHierarchicalEntity, TNew](legacyExpos: List[TLegacy],
    idMap: Map[Int, Option[Long]], conversion: (Option[Long], TLegacy) => TNew,
    inserter: (TNew) => Long)(implicit session: PostgresDriver.simple.Session): List[(Int, Long)] = {
    if (legacyExpos.length == 0) List() else {
      val (toInsert, toDefer) = legacyExpos partition { l => l.parentId map { idMap contains _ } getOrElse true }
      val inserted = toInsert map { l =>
        val parentId = l.parentId match {
          case None => None
          case Some(p) => idMap.get(p).getOrElse(None)
        }

        (conversion(parentId, l), l.id)
      } map { tuple =>
        val (nv, legacyId) = tuple
        val id = inserter(nv)
        (legacyId, id)
      }

      val mapToAdd = inserted map { t => (t._1, Some(t._2)) }

      inserted ++ insertHierarchy(toDefer, idMap ++ mapToAdd.toMap, conversion, inserter)
    }
  }

  val (depositsPlaces, expositions, categories, testimonials, articles, specimens, specimenPhotos) = LegacyDatabase withSession { source =>
    (
      LegacyDepositsPlaces.findAll(source),
      LegacyExpositions.findAll(source),
      LegacyCategories.findAll(source),
      LegacyTestimonials.findAll(source),
      LegacyArticles.findAll(source),
      LegacySpecimens.findAll(source),
      LegacySpecimenPhotos.findAll(source))
  }

  TargetDatabase withSession { implicit target =>
    SpecimenPhotos.deleteAll
    Specimens.deleteAll
    Expositions.deleteAll
    DepositsPlaces.deleteAll
    Categories.deleteAll
    Testimonials.deleteAll
    Articles.deleteAll
    Images.deleteAll

    val placesIdMap = depositsPlaces map (l => (NewDepositsPlace(l.name, l.description), l)) map { p: (NewDepositsPlace, LegacyDepositsPlace) =>
      val (place, l) = p
      (l.id, DepositsPlaces add place)
    } toMap

    testimonials map { t => NewTestimonial(t.authorName, t.authorEmail, t.text, t.addedAt, t.isApproved) } foreach { Testimonials add _ }

    articles map (a => (NewImage(a.imageUrl, a.addedAt), a)) map { p =>
      val (i, a) = p
      val imageId = Images add i
      NewArticle(a.title, a.summary, a.text, imageId, a.addedAt)
    } foreach { Articles add _ }

    val expoConverter = (parentId: Option[Long], l: LegacyExposition) => NewExposition(parentId, l.name, l.description, l.weight)
    val expoIdMap = insertHierarchy(expositions, Map(0 -> None), expoConverter, (nl: NewExposition) => Expositions.add(nl)).toMap

    val catConverter = (parentId: Option[Long], l: LegacyCategory) => NewCategory(parentId, l.name, !l.showOnSite, l.weight)
    val catIdMap = insertHierarchy(categories, Map(0 -> None), catConverter, (nl: NewCategory) => Categories.add(nl)).toMap

    val specimensIdMap = specimens map { l =>
      val ns = NewSpecimen(l.name, l.nameLatin, l.size, l.formula, l.age, l.label, l.shortDescription, l.description, l.showOnSite, l.priority,
        catIdMap.get(l.categoryId).get, expoIdMap.get(l.expositionId).get, placesIdMap.get(l.depositsPlaceId).get)
      val id = Specimens add ns
      (l.id, id)
    } toMap

    var skipped = 0
    specimenPhotos foreach { l =>
      val imageId = Images add NewImage(l.imageUrl)
      val specimenId = specimensIdMap.get(Some(l.specimenId))
      specimenId match {
        case None => {
          log.warn("No specimen found for legacy id: %d".format(l.specimenId))
          skipped = skipped + 1
        }
        case Some(sid) => {
          val p = NewSpecimenPhoto(sid, imageId, l.isMain)
          SpecimenPhotos add p
        }
      }
    }

    log.info("Specimen photos imported, skipped %d".format(skipped))
  }

  log info "All done"
}
