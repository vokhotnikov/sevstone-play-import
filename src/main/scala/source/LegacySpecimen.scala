package source

import scala.slick.driver.MySQLDriver.simple._

case class LegacySpecimen(name: String, nameLatin: Option[String], size: Option[String], formula: Option[String], age: Option[String],
    label: String, shortDescription: String, description: String, showOnSite: Boolean, priority: Int,
    categoryId: Int, expositionId: Int, depositsPlaceId: Int, id: Option[Int] = None)
    

object LegacySpecimens extends Table[LegacySpecimen]("item") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def nameLatin = column[Option[String]]("namelat")
  def size = column[Option[String]]("size")
  def formula = column[Option[String]]("formula")
  def age = column[Option[String]]("age")
  
  def label = column[String]("etiket", O.NotNull, O.DBType("mediumtext"))
  def shortDescription = column[String]("shortdescr", O.NotNull, O.DBType("mediumtext"))
  def description = column[String]("description", O.NotNull, O.DBType("mediumtext"))
  
  def showOnSite = column[Boolean]("hot", O.NotNull)
  def priority = column[Int]("weight", O.NotNull)
  
  def categoryId = column[Int]("category_id", O.NotNull)
  def expositionId = column[Int]("expo_id", O.NotNull)
  def depositsPlaceId = column[Int]("mesto_id", O.NotNull)
  
  def * = name ~ nameLatin ~ size ~ formula ~ age ~ label ~ shortDescription ~ description ~ showOnSite ~ priority ~ categoryId ~ expositionId ~ depositsPlaceId ~ id.? <> (LegacySpecimen, LegacySpecimen.unapply _)

  def findAll(implicit session: Session) = Query(LegacySpecimens).list
}