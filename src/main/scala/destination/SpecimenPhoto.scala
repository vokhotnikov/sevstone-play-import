package destination

import scala.slick.driver.PostgresDriver.simple._

case class SpecimenPhoto(specimenId: Long, imageId: Long, isMain: Boolean, id: Option[Long] = None)
case class NewSpecimenPhoto(specimenId: Long, imageId: Long, isMain: Boolean)

object SpecimenPhotos extends Table[SpecimenPhoto]("specimen_photos") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def specimenId = column[Long]("specimen_id", O.NotNull)
  def imageId = column[Long]("image_id", O.NotNull)
  def isMain = column[Boolean]("is_main", O.NotNull)
  
  def specimen = foreignKey("SpecimenPhotos_SpecimenFK", specimenId, Specimens)(_.id)
  def image = foreignKey("SpecimenPhotos_ImageFK", imageId, Images)(_.id)

  def * = specimenId ~ imageId ~ isMain ~ id.? <> (SpecimenPhoto, SpecimenPhoto.unapply _)
  def forInsert = specimenId ~ imageId ~ isMain <> (NewSpecimenPhoto, NewSpecimenPhoto.unapply _) returning id
  
  def add(p: NewSpecimenPhoto)(implicit session: Session) = forInsert insert p
  
  def deleteAll(implicit session: Session) = Query(SpecimenPhotos).delete
}