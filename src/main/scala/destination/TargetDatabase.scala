package destination

import scala.slick.driver.PostgresDriver.simple._

object TargetDatabase {
  def withSession[Res](f: Session => Res) = {
    Database.forURL("jdbc:postgresql://rserv/sevstonedev?user=sevstonedev&password=sevstonedev", driver = "org.postgresql.Driver") withSession f
  }
}
