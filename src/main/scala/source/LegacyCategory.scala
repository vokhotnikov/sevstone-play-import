package source

import scala.slick.driver.MySQLDriver.simple._

case class LegacyCategory(id: Int, parentId: Option[Int], name: String, showOnSite: Boolean, weight:Int) extends LegacyHierarchicalEntity

object LegacyCategories extends Table[LegacyCategory]("item_category") {
  def id = column[Int]("id", O.PrimaryKey);
  def parentId = column[Option[Int]]("parent_id")
  def name = column[String]("name")
  def showOnSite = column[Boolean]("showOnSite")
  def weight = column[Int]("weight")
  
  def * = id ~ parentId ~ name ~ showOnSite ~ weight <> (LegacyCategory.apply _, LegacyCategory.unapply _)
  
  def findAll(implicit session: Session) = Query(LegacyCategories).list 
}