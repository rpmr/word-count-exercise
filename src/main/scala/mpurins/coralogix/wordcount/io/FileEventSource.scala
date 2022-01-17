package mpurins.coralogix.wordcount.io

import cats.Applicative
import fs2.io.file.{Files, Path}
import io.circe.{Decoder, Parser}
import org.typelevel.log4cats.Logger

import java.nio.file.{Path => JPath}

object FileEventSource {
  def apply[F[_]: Applicative: Files, Event: Decoder](
      path: JPath,
      parser: Parser,
      logger: Logger[F]
  ): fs2.Stream[F, Event] =
    Files[F]
      .readAll(Path.fromNioPath(path))
      .through(ByteStreamEventSource(parser, logger))
}
