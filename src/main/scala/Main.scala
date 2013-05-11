import org.slf4j.LoggerFactory

object Main extends App {
  def log = LoggerFactory.getLogger("ImportTool")
  println("It's alive!")

  log info "All done"
}
