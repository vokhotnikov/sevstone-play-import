import org.slf4j.LoggerFactory

trait Logging {
  def log = LoggerFactory.getLogger(getClass)
}
