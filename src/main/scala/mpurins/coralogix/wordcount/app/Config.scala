package mpurins.coralogix.wordcount.app

import com.comcast.ip4s.Host
import mpurins.coralogix.wordcount.io.WordEventSource
import mpurins.coralogix.wordcount.services.{EventCounter, HttpServer}
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert
import pureconfig.generic.semiauto.deriveReader
import pureconfig.module.ip4s._

final case class Config(
    http: HttpServer.Config,
    source: WordEventSource.Config,
    counter: EventCounter.Config
)

object Config {
  implicit val configReader: ConfigReader[Config] = {
    implicit val hostReader: ConfigReader[Host] = ConfigReader[String].emap { value =>
      Host.fromString(value).toRight(CannotConvert(value, "Host", "Not a valid host string"))
    }
    implicit val httpServerConfigReader: ConfigReader[HttpServer.Config] = deriveReader
    implicit val wordEventConfigReader: ConfigReader[WordEventSource.Config] = {
      val stdInConfigReader = ConfigReader[String].emap { value =>
        Either.cond(
          value.compareToIgnoreCase(WordEventSource.Config.StdIn.toString) == 0, // scalafix:ok
          WordEventSource.Config.StdIn,
          CannotConvert(value, "StdIn", "Not a valid StdIn identifier")
        )
      }
      val fileConfigReader = deriveReader[WordEventSource.Config.File]

      stdInConfigReader.orElse(fileConfigReader)
    }
    implicit val eventCounterConfigReader: ConfigReader[EventCounter.Config] = deriveReader

    deriveReader
  }
}
