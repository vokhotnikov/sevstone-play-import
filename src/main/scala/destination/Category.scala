package destination

import scala.slick.driver.PostgresDriver.simple._

case class NewCategory(parentId: Option[Long], title: String, isHidden: Boolean, sortPriority: Long)
case class Category(id: Long, parentId: Option[Long], title: String, isHidden: Boolean, sortPriority: Long) {
  def asNew = NewCategory(parentId, title, isHidden, sortPriority)
}

object Categories extends Table[Category]("categories") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def parentId = column[Option[Long]]("parent_id")
  def title = column[String]("title", O.NotNull)
  def isHidden = column[Boolean]("is_hidden", O.NotNull)
  def sortPriority = column[Long]("sort_priority", O.NotNull, O.Default(0))

  def parent = foreignKey("Categories_ParentFK", parentId, Categories)(_.id)

  def * = id ~ parentId ~ title ~ isHidden ~ sortPriority <> (Category, Category.unapply _)

  def forInsert = parentId ~ title ~ isHidden ~ sortPriority <> (NewCategory, NewCategory.unapply _) returning id

  def add(newCategory: NewCategory)(implicit session: Session) = forInsert insert newCategory
  
  def deleteAll(implicit session: Session) = Query(Categories).delete
}