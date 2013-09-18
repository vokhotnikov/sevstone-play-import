package destination

import scala.slick.driver.PostgresDriver.simple._

case class Specimen(name: String, nameLatin: Option[String], size: Option[String], formula: Option[String], age: Option[String],
    label: String, shortDescription: String, description: String, showOnSite: Boolean, priority: Int,
    categoryId: Long, expositionId: Long, depositsPlaceId: Long, id: Option[Long] = None)

case class NewSpecimen(name: String, nameLatin: Option[String], size: Option[String], formula: Option[String], age: Option[String],
    label: String, shortDescription: String, description: String, showOnSite: Boolean, priority: Int,
    categoryId: Long, expositionId: Long, depositsPlaceId: Long)

object Specimens extends Table[Specimen]("specimens") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def nameLatin = column[Option[String]]("name_latin")
  def size = column[Option[String]]("size")
  def formula = column[Option[String]]("formula")
  def age = column[Option[String]]("age")
  
  def label = column[String]("label", O.NotNull, O.DBType("text"))
  def shortDescription = column[String]("short_description", O.NotNull, O.DBType("text"))
  def description = column[String]("description", O.NotNull, O.DBType("text"))
  
  def showOnSite = column[Boolean]("show_on_site", O.NotNull)
  def priority = column[Int]("priority", O.NotNull)
  
  def categoryId = column[Long]("category_id", O.NotNull)
  def expositionId = column[Long]("exposition_id", O.NotNull)
  def depositsPlaceId = column[Long]("deposits_place_id", O.NotNull)
  
  def category = foreignKey("Specimens_CategoryFK", categoryId, Categories)(_.id)
  def exposition = foreignKey("Specimens_ExpositionFK", expositionId, Expositions)(_.id)
  def depositsPlace = foreignKey("Specimens_DepositsPlaceFK", depositsPlaceId, DepositsPlaces)(_.id)
  
  def * = name ~ nameLatin ~ size ~ formula ~ age ~ label ~ shortDescription ~ description ~ showOnSite ~ priority ~ categoryId ~ expositionId ~ depositsPlaceId ~ id.? <> (Specimen, Specimen.unapply _)

  def forInsert = name ~ nameLatin ~ size ~ formula ~ age ~ label ~ shortDescription ~ description ~ showOnSite ~ priority ~ categoryId ~ expositionId ~ depositsPlaceId <> (NewSpecimen, NewSpecimen.unapply _) returning id
  
  def add(s: NewSpecimen)(implicit session: Session) = forInsert insert s
  
  def deleteAll(implicit session: Session) = Query(Specimens).delete
}