import source._
import destination._

object Main extends App with Logging {
  val depositsPlaces = LegacyDatabase withSession { source =>
    LegacyDepositsPlaces.findAll(source)
  }

  TargetDatabase withSession { implicit target =>
    DepositsPlaces.deleteAll

    depositsPlaces map { l =>
      NewDepositsPlace(l.name, l.description)
      } foreach { DepositsPlaces add _ }
  }

  log info "All done"
}
