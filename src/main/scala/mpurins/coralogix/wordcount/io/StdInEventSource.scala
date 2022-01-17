package mpurins.coralogix.wordcount.io

import cats.effect.kernel.Sync
import fs2.Stream
import io.circe.{Decoder, Parser}
import org.typelevel.log4cats.Logger

object StdInEventSource {

  def apply[F[_]: Sync, Event: Decoder](
      parser: Parser,
      logger: Logger[F]
  ): Stream[F, Event] =
    fs2.io
      .stdin(4096)
      .through(ByteStreamEventSource(parser, logger))
}
