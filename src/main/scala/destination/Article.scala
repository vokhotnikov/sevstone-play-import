package destination

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

case class Article(title: String, summary: String, text: String, imageId: Long, addedAtRaw: Timestamp, id: Option[Long] = None) 
case class NewArticle(title: String, summary: String, text: String, imageId: Long, addedAtRaw: Timestamp) 

object Articles extends Table[Article]("articles") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def summary = column[String]("summary", O.NotNull, O.DBType("text"))
  def text = column[String]("text", O.NotNull, O.DBType("text"))
  def imageId = column[Long]("image_id", O.NotNull)
  def addedAt = column[Timestamp]("added_at", O.NotNull)
  
  def image = foreignKey("Articles_ImageFK", imageId, Images)(_.id)
  
  def * = title ~ summary ~ text ~ imageId ~ addedAt ~ id.? <> (Article, Article.unapply _)
  def forInsert = title ~ summary ~ text ~ imageId ~ addedAt <> (NewArticle, NewArticle.unapply _) returning id
  
  def add(a: NewArticle)(implicit session: Session) = forInsert insert a
  
  def deleteAll(implicit session: Session) = Query(Articles).delete
}