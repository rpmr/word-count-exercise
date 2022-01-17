package mpurins.coralogix.wordcount.services

import fs2.{Pure, Stream}
import mpurins.coralogix.wordcount.models.Window
import mpurins.coralogix.wordcount.services.EventCounterSpec.Event
import munit.FunSuite

import java.time.Instant
import scala.concurrent.duration._

final class EventCounterSpec extends FunSuite {
  test("EventCounter must count events and emit Windows") {
    val epoch = Instant.EPOCH
    val source = Stream(
      Event("a", 1L, epoch),
      Event("a", 2L, epoch.plusSeconds(1)),
      Event("b", 3L, epoch.plusSeconds(2)),
      Event("a", 2L, epoch.plusSeconds(10)),
      Event("a", 10L, epoch),
      Event("b", 1L, epoch.plusSeconds(11))
    )

    val counter = EventCounter[Pure, Event, String](
      config = EventCounter.Config(10.seconds),
      extractKey = _.key,
      extractTime = _.timestamp,
      countFn = _.count
    )

    val result: List[Window[Map[String, Long]]] = source
      .through(counter)
      .compile
      .toList

    val expected = List(
      Window(
        opensWhen = epoch,
        closesWhen = epoch.plusSeconds(10),
        data = Map("a" -> 1L)
      ),
      Window(
        opensWhen = epoch,
        closesWhen = epoch.plusSeconds(10),
        data = Map("a" -> 3L)
      ),
      Window(
        opensWhen = epoch,
        closesWhen = epoch.plusSeconds(10),
        data = Map("a" -> 3L, "b" -> 3L)
      ),
      Window(
        opensWhen = epoch.plusSeconds(10),
        closesWhen = epoch.plusSeconds(20),
        data = Map("a" -> 2L)
      ),
      Window(
        opensWhen = epoch.plusSeconds(10),
        closesWhen = epoch.plusSeconds(20),
        data = Map("a" -> 2L, "b" -> 1L)
      )
    )

    assertEquals(result, expected)
  }
}

object EventCounterSpec {
  final case class Event(
      key: String,
      count: Long,
      timestamp: Instant
  )
}
