package mpurins.coralogix.wordcount.endpoints

import cats.effect.IO
import cats.syntax.all._
import io.circe.Json
import mpurins.coralogix.wordcount.services.WordCountService
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.{Method, Request}
import org.scalacheck.effect.PropF
import sttp.tapir.server.http4s.Http4sServerInterpreter

final class WordCountEndpointSpec extends CatsEffectSuite with ScalaCheckEffectSuite {
  test("create response") {
    PropF.forAllF { data: Map[String, Map[String, Long]] =>
      val endpoint = WordCountEndpoint(new WordCountService[IO] {
        override def current: IO[Map[String, Map[String, Long]]] = IO.pure(data)
      })
      Http4sServerInterpreter[IO]()
        .toRoutes(endpoint)
        .orNotFound
        .run {
          Request(
            method = Method.GET,
            uri = uri"/current"
          )
        }
        .flatMap(_.as[Json])
        .flatMap(_.as[WordCountEndpoint.Response].liftTo[IO])
        .map { response =>
          val value = response.data.groupMapReduce(_.eventType)(
            _.words.groupMapReduce(_.word)(_.count)(_ + _)
          )(_ ++ _)
          assertEquals(value, data)
        }
    }
  }
}
