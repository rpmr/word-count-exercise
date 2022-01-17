package mpurins.coralogix.wordcount.io

import cats.effect.kernel.Sync
import fs2.io.stdout
import fs2.{INothing, Pipe, text}
import io.circe.syntax._
import io.circe.{Encoder, Printer}

object StdOutSink {
  private val newline = System.lineSeparator()
  def apply[F[_]: Sync, Event: Encoder](printer: Printer): Pipe[F, Event, INothing] =
    _.map { event =>
      printer.print(event.asJson) + newline
    }
      .through(text.utf8.encode[F])
      .through(stdout)
}
