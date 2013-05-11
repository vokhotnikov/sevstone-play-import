package source

import scala.slick.driver.MySQLDriver.simple._

object LegacyDatabase {
  def withSession[Res](f: Session => Res) = {
    Database.forURL("jdbc:mysql://rserv/sevstone-dev?user=sevstone-dev&password=sevstone-dev", driver = "com.mysql.jdbc.Driver") withSession f
  }
}
