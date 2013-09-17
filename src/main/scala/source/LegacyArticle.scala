package source

import scala.slick.driver.MySQLDriver.simple._
import java.sql.Timestamp

case class LegacyArticle(id: Int, addedAt: Timestamp, imageUrl: String, title: String, text: String, summary: String)

object LegacyArticles extends Table[LegacyArticle]("articles") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def addedAt = column[Timestamp]("date_created", O.NotNull)
  def imageUrl = column[String]("image", O.NotNull)
  def title = column[String]("title_l1", O.NotNull)
  def text = column[String]("text_l1", O.NotNull, O.DBType("longtext"))
  def summary = column[String]("header", O.NotNull, O.DBType("mediumtext"))

  def * = id ~ addedAt ~ imageUrl ~ title ~ text ~ summary <> (LegacyArticle, LegacyArticle.unapply _)
  
  def findAll(implicit session: Session) = Query(LegacyArticles).list
}