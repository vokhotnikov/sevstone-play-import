import source._
import destination._

import scala.slick.driver.PostgresDriver

object Main extends App with Logging {
  def insertExpositions(legacyExpos: List[LegacyExposition], idMap: Map[Int, Option[Long]])(implicit session: PostgresDriver.simple.Session): List[Long] = {
    if (legacyExpos.length == 0) List() else {
      val (toInsert, toDefer) = legacyExpos partition { l => l.parentId map { idMap contains _ } getOrElse true }
      val inserted = toInsert map { l =>
        val parentId = l.parentId match {
          case None => None
          case Some(p) => idMap.get(p).getOrElse(None)
        }

        (NewExposition(parentId, l.name, l.description, l.weight), l.id)
      } map { tuple =>
        val (nv, legacyId) = tuple
        val id = Expositions add nv
        (legacyId, id)
      }

      val mapToAdd = inserted map { t => (t._1, Some(t._2)) }

      inserted.map{ _._2 } ++ insertExpositions(toDefer, idMap ++ mapToAdd.toMap)
    }
  }

  val (depositsPlaces, expositions) = LegacyDatabase withSession { source => (
    LegacyDepositsPlaces.findAll(source),
    LegacyExpositions.findAll(source)
  )}

  TargetDatabase withSession { implicit target =>
    Expositions.deleteAll
    DepositsPlaces.deleteAll

    depositsPlaces map { l =>
      NewDepositsPlace(l.name, l.description)
      } foreach { DepositsPlaces add _ }

      insertExpositions(expositions, Map(0 -> None))
  }

  log info "All done"
}
