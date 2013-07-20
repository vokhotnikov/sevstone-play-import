package source

trait LegacyHierarchicalEntity {
	def id(): Int
	def parentId(): Option[Int]
}