package source

import scala.slick.driver.MySQLDriver.simple._

case class LegacySpecimenPhoto(specimenId: Int, imageUrl: String, isMain: Boolean, id: Option[Int] = None)

object LegacySpecimenPhotos extends Table[LegacySpecimenPhoto]("item_photo") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def specimenId = column[Int]("item_id", O.NotNull)
  def imageUrl = column[String]("filename", O.NotNull)
  def isMain = column[Boolean]("isMain", O.NotNull)
  
  def * = specimenId ~ imageUrl ~ isMain ~ id.? <> (LegacySpecimenPhoto, LegacySpecimenPhoto.unapply _)
  
  def findAll(implicit session: Session) = Query(LegacySpecimenPhotos).list
}