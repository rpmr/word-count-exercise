package mpurins.coralogix.wordcount.services

import cats.effect.SyncIO
import cats.syntax.flatMap._
import cats.syntax.option._
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.effect.PropF

final class ObserverSpec extends CatsEffectSuite with ScalaCheckEffectSuite {

  test("current is empty") {
    Observer
      .make[SyncIO, Int]
      .flatMap(_.current)
      .map { value =>
        assertEquals(value, none)
      }
  }

  test("current after get is set") {
    PropF.forAllF { data: Int =>
      Observer
        .make[SyncIO, Int]
        .flatTap(_.set(data))
        .flatMap(_.current)
        .map { value =>
          assertEquals(value, data.some)
        }
    }
  }

  test("set overrides existing value") {
    PropF.forAllF { (data: Int, secondData: Int) =>
      Observer
        .make[SyncIO, Int]
        .flatTap(_.set(data))
        .flatTap(_.set(secondData))
        .flatMap(_.current)
        .map { value =>
          assertEquals(value, secondData.some)
        }
    }
  }
}
