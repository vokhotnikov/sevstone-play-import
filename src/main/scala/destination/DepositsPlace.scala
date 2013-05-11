package source

import scala.slick.driver.PostgresDriver.simple._

case class NewDepositsPlace(title: String, description: String)

case class DepositsPlace(id: Long, title: String, description: String)

object DepositsPlaces extends Table[DepositsPlace]("deposits_places") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def description = column[String]("description")

  def * = id ~ title ~ description <> (DepositsPlace.apply _, DepositsPlace.unapply _)

  def forInsert = title ~ description <> (NewDepositsPlace.apply _, NewDepositsPlace.unapply _) returning id

  def add(newValue: NewDepositsPlace)(implicit session: Session) = forInsert insert newValue

  def deleteAll(implicit session: Session) = Query(DepositsPlaces).delete
}
