package destination

import scala.slick.driver.PostgresDriver.simple._

case class NewExposition(parentId: Option[Long], title: String, description: String, sortPriority: Long)
case class Exposition(id: Long, parentId: Option[Long], title: String, description: String, sortPriority: Long)

object Expositions extends Table[Exposition]("expositions") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def parentId = column[Option[Long]]("parent_id")
  def title = column[String]("title")
  def description = column[String]("description")
  def sortPriority = column[Long]("sort_priority")

  def * = id ~ parentId ~ title ~ description ~ sortPriority <> (Exposition.apply _, Exposition.unapply _)

  def forInsert = parentId ~ title ~ description ~ sortPriority <> (NewExposition.apply _, NewExposition.unapply _) returning id

  def add(newValue: NewExposition)(implicit session: Session) = forInsert insert newValue

  def deleteAll(implicit session: Session) = Query(Expositions).delete
}
