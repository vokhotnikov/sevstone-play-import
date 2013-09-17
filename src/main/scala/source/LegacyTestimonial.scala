package source

import scala.slick.driver.MySQLDriver.simple._
import java.sql.Date
import java.sql.Timestamp

case class LegacyTestimonial(id: Int, authorName: String, addedAt: Timestamp, text: String, isApproved: Boolean, authorEmail: Option[String])

object LegacyTestimonials extends Table[LegacyTestimonial]("testimonials") {
  def id = column[Int]("id", O.PrimaryKey)
  def authorName = column[String]("author")
  def addedAt = column[Timestamp]("date_created")
  def text = column[String]("text_l1", O.DBType("mediumtext"))
  def isApproved = column[Boolean]("approved")
  def authorEmail = column[Option[String]]("email")

  def * = id ~ authorName ~ addedAt ~ text ~ isApproved ~ authorEmail <> (LegacyTestimonial.apply _, LegacyTestimonial.unapply _)

  def findAll(implicit session: Session) = Query(LegacyTestimonials).list
}