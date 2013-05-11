package source

import scala.slick.driver.MySQLDriver.simple._

case class LegacyExposition(id: Int, parentId: Option[Int], name: String, description: String, weight: Int)

object LegacyExpositions extends Table[LegacyExposition]("item_expo") {
  def id = column[Int]("id", O.PrimaryKey)
  def parentId = column[Option[Int]]("parent_id")
  def name = column[String]("name")
  def description = column[String]("description")
  def weight = column[Int]("weight")

  def * = id ~ parentId ~ name ~ description ~ weight <> (LegacyExposition.apply _, LegacyExposition.unapply _)

  def findAll(implicit session: Session) = Query(LegacyExpositions).list
}

