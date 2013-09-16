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

object Main extends App with Logging {
    def insertHierarchy[TLegacy <: LegacyHierarchicalEntity, TNew](legacyExpos: List[TLegacy], 
        idMap: Map[Int, Option[Long]], conversion: (Option[Long], TLegacy) => TNew, 
        inserter: (TNew) => Long)(implicit session: PostgresDriver.simple.Session): List[Long] = {
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

            inserted.map { _._2 } ++ insertHierarchy(toDefer, idMap ++ mapToAdd.toMap, conversion, inserter)
        }
    }

    val (depositsPlaces, expositions, categories) = LegacyDatabase withSession { source =>
        (
            LegacyDepositsPlaces.findAll(source),
            LegacyExpositions.findAll(source),
            LegacyCategories.findAll(source))
    }

    TargetDatabase withSession { implicit target =>
        Expositions.deleteAll
        DepositsPlaces.deleteAll

        depositsPlaces map { l => NewDepositsPlace(l.name, l.description) } foreach { DepositsPlaces add _ }

        val expoConverter = (parentId: Option[Long], l: LegacyExposition) => NewExposition(parentId, l.name, l.description, l.weight)
        insertHierarchy(expositions, Map(0 -> None), expoConverter, (nl: NewExposition) => Expositions.add(nl))
        
        val catConverter = (parentId: Option[Long], l: LegacyCategory) => NewCategory(parentId, l.name, !l.showOnSite, l.weight)
        insertHierarchy(categories, Map(0 -> None), catConverter, (nl: NewCategory) => Categories.add(nl))

    }

    log info "All done"
}
