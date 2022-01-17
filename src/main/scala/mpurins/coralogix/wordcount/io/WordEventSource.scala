package mpurins.coralogix.wordcount.io

import cats.effect.kernel.Sync
import fs2.io.file.Files
import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import io.circe.jawn.JawnParser
import mpurins.coralogix.wordcount.models.Event
import org.typelevel.log4cats.Logger

import java.nio.file.Path
import java.time.Instant

object WordEventSource {
  sealed trait Config extends Product with Serializable
  object Config {
    final case class File(path: Path) extends Config
    final case object StdIn           extends Config
  }

  private implicit val eventDecoder: Decoder[Event] = {
    implicit val config: Configuration            = Configuration.default.withSnakeCaseMemberNames
    implicit val instantDecoder: Decoder[Instant] = Decoder[Long].map(Instant.ofEpochSecond)
    deriveConfiguredDecoder
  }

  def apply[F[_]: Files: Sync](
      config: Config,
      logger: Logger[F]
  ): fs2.Stream[F, Event] = {
    val parser = JawnParser(allowDuplicateKeys = true)
    config match {
      case Config.File(path) => FileEventSource(path, parser, logger)
      case Config.StdIn      => StdInEventSource(parser, logger)
    }
  }
}
