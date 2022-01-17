package mpurins.coralogix.wordcount.services

import cats.syntax.option._
import cats.syntax.partialOrder._
import cats.syntax.semigroup._
import fs2.Pipe
import mpurins.coralogix.wordcount.models.Window
import org.typelevel.cats.time._

import java.time.{Duration, Instant}
import scala.concurrent.duration.FiniteDuration
import scala.jdk.DurationConverters._

object EventCounter {
  final case class Config(windowDuration: FiniteDuration)

  def apply[F[_], Event, Key](
      config: Config,
      extractKey: Event => Key,
      extractTime: Event => Instant,
      countFn: Event => Long
  ): Pipe[F, Event, Window[Map[Key, Long]]] = {
    val windowDuration = config.windowDuration.toJava

    _.scan(none[Window[Map[Key, Long]]]) { (previous, event) =>
      val time = extractTime(event)
      val window = previous
        .fold {
          Window(
            opensWhen = time,
            closesWhen = time.plus(windowDuration),
            data = Map.empty[Key, Long]
          )
        } { candidate =>
          if (time >= candidate.closesWhen) {
            val opensWhen = candidate.closesWhen.plus(
              windowDuration.multipliedBy(
                Duration.between(candidate.closesWhen, time).dividedBy(windowDuration)
              )
            )
            Window(
              opensWhen = opensWhen,
              closesWhen = opensWhen.plus(windowDuration),
              data = Map.empty[Key, Long]
            )
          } else candidate
        }

      if (time >= window.opensWhen) {
        window
          .copy(
            data = window.data.combine(Map(extractKey(event) -> countFn(event)))
          )
          .some
      } else window.some
    }.unNone.changes
  }
}
