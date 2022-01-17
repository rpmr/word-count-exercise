package mpurins.coralogix.wordcount.endpoints

import cats.Functor
import cats.syntax.either._
import cats.syntax.functor._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import mpurins.coralogix.wordcount.services.WordCountService
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

object WordCountEndpoint {
  final case class Response(data: List[WordsByEvent])
  private object Response {
    implicit val encoder: Encoder[Response] = deriveEncoder
    implicit val decoder: Decoder[Response] = deriveDecoder
    implicit val schema: Schema[Response]   = Schema.derived
  }

  final case class WordsByEvent(eventType: String, words: List[WordCount])
  private object WordsByEvent {
    implicit val encoder: Encoder[WordsByEvent] = deriveEncoder
    implicit val decoder: Decoder[WordsByEvent] = deriveDecoder
    implicit val schema: Schema[WordsByEvent]   = Schema.derived
  }

  final case class WordCount(word: String, count: Long)
  private object WordCount {
    implicit val encoder: Encoder[WordCount] = deriveEncoder
    implicit val decoder: Decoder[WordCount] = deriveDecoder
    implicit val schema: Schema[WordCount]   = Schema.derived
  }

  private val currentCount = endpoint.get
    .in("current")
    .out(jsonBody[Response])

  def apply[F[_]: Functor](
      service: WordCountService[F]
  ): ServerEndpoint.Full[Unit, Unit, Unit, Unit, Response, Any, F] =
    currentCount.serverLogic[F] { _ =>
      service.current.map { data =>
        Response(data.view.map {
          case (eventType, words) =>
            WordsByEvent(
              eventType = eventType,
              words = words.view.map {
                case (word, count) =>
                  WordCount(
                    word = word,
                    count = count
                  )
              }.toList
            )
        }.toList).asRight
      }
    }
}
