package destination

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Date
import java.sql.Timestamp

case class Testimonial(authorName: String, authorEmail: Option[String], text: String, addedAt: Timestamp, isApproved: Boolean, id: Option[Long] = None)
case class NewTestimonial(authorName: String, authorEmail: Option[String], text: String, addedAt: Timestamp, isApproved: Boolean)

object Testimonials extends Table[Testimonial]("testimonials") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def authorName = column[String]("author_name", O.NotNull)
  def authorEmail = column[Option[String]]("author_email")
  def text = column[String]("text", O.NotNull, O.DBType("text"))
  def addedAt = column[Timestamp]("added_at", O.NotNull)
  def isApproved = column[Boolean]("is_approved", O.NotNull)
  
  def * = authorName ~ authorEmail ~ text ~ addedAt ~ isApproved ~ id.? <> (Testimonial, Testimonial.unapply _)
  
  def autoInc = authorName ~ authorEmail ~ text ~ addedAt ~ isApproved <> (NewTestimonial, NewTestimonial.unapply _) returning id
  
  def add(t: NewTestimonial)(implicit session: Session) = autoInc insert t
  
  def deleteAll(implicit session: Session) = Query(Testimonials).delete
}