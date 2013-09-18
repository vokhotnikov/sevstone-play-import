package destination

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

case class Image(imageUrl: String, addedAt: Timestamp, id: Option[Long] = None)
case class NewImage(imageUrl: String, addedAt: Timestamp = new Timestamp(new java.util.Date().getTime()))


object Images extends Table[Image]("images") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def url = column[String]("url", O.NotNull)
  def addedAt = column[Timestamp]("added_at", O.NotNull)
  
  def * = url ~ addedAt ~ id.? <> (Image, Image.unapply _)
  def forInsert = url ~ addedAt <> (NewImage, NewImage.unapply _) returning id
  
  def add(i: NewImage)(implicit session: Session) = forInsert insert i
  
  def deleteAll(implicit session: Session) = Query(Images).delete
}