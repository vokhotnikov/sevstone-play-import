package source

import scala.slick.driver.MySQLDriver.simple._

case class LegacyDepositsPlace(id: Int, name: String, description: String)

object LegacyDepositsPlaces extends Table[LegacyDepositsPlace]("item_mesto") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def description = column[String]("description")

  def * = id ~ name ~ description <> (LegacyDepositsPlace.apply _, LegacyDepositsPlace.unapply _)

  def findAll(implicit session: Session) = Query(LegacyDepositsPlaces).list
}
