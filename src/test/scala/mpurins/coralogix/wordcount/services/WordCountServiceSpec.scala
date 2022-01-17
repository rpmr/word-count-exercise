package mpurins.coralogix.wordcount.services

import cats.effect.SyncIO
import cats.syntax.flatMap._
import mpurins.coralogix.wordcount.models.Window
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.effect.PropF

import java.time.Instant

final class WordCountServiceSpec extends CatsEffectSuite with ScalaCheckEffectSuite {

  test("return current counts") {
    PropF.forAllF { data: Map[String, Map[String, Long]] =>
      Observer
        .make[SyncIO, Window[Map[(String, String), Long]]]
        .flatTap(
          _.set(
            Window(
              opensWhen = Instant.EPOCH,
              closesWhen = Instant.EPOCH,
              data = data.view.flatMap {
                case (eventType, counts) =>
                  counts.view.map {
                    case (data, count) => (eventType, data) -> count
                  }
              }.toMap
            )
          )
        )
        .map(WordCountService(_))
        .flatMap(_.current)
        .map { value =>
          assertEquals(
            value,
            data.filterNot {
              case (_, value) => value.isEmpty
            }
          )
        }
    }
  }
}
