package mpurins.coralogix.wordcount.io

import cats.Applicative
import cats.syntax.bitraverse._
import fs2.{Pipe, text}
import io.circe.{Decoder, Parser}
import org.typelevel.log4cats.Logger

private object ByteStreamEventSource {
  def apply[F[_]: Applicative, Event: Decoder](
      parser: Parser,
      logger: Logger[F]
  ): Pipe[F, Byte, Event] =
    _.through(text.utf8.decode[F])
      .through(text.lines)
      .map { line =>
        parser.decode[Event](line).left.map(failure => (failure, line))
      }
      .evalTapChunk(_.leftTraverse {
        case (failure, line) =>
          logger.info(s"Invalid input ($line), parsing failed with ${failure.getMessage}")
      })
      .collect {
        case Right(event) => event
      }
}
