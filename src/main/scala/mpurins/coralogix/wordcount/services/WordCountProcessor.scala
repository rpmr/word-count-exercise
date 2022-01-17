package mpurins.coralogix.wordcount.services

import cats.effect.kernel.{Async, Resource}
import cats.effect.syntax.spawn._
import cats.syntax.flatMap._
import cats.syntax.option._
import cats.syntax.partialOrder._
import fs2.Stream
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Encoder, KeyEncoder, Printer}
import mpurins.coralogix.wordcount.io.StdOutSink
import mpurins.coralogix.wordcount.models.{Event, Window}
import org.typelevel.cats.time._
import org.typelevel.log4cats.Logger

object WordCountProcessor {

  private implicit val windowEncoder: Encoder[Window[Map[(String, String), Long]]] = {
    implicit val stringTupleKeyEncoder: KeyEncoder[(String, String)] =
      KeyEncoder[String].contramap(_.toString)
    deriveEncoder
  }

  def resource[F[_]: Async](
      source: Stream[F, Event],
      counterConfig: EventCounter.Config,
      observer: Observer[F, Window[Map[(String, String), Long]]],
      logger: Logger[F]
  ): Resource[F, Unit] = {
    val counter = EventCounter[F, Event, (String, String)](
      config = counterConfig,
      extractKey = event => (event.eventType, event.data),
      extractTime = _.timestamp,
      countFn = _ => 1L
    )

    source
      .through(counter)
      .evalTapChunk(observer.set)
      .zipWithNext
      .map {
        case (window, next) =>
          next
            .filterNot(_.opensWhen > window.opensWhen)
            .fold(window.some)(_ => none)
      }
      .unNone
      .through(StdOutSink(Printer.spaces2))
      .compile
      .drain
      .background >> Resource.eval(logger.info("Event processing started"))
  }
}
