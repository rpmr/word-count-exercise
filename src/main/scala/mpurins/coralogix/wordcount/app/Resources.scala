package mpurins.coralogix.wordcount.app

import cats.effect.kernel.{Async, Resource}
import cats.syntax.apply._
import mpurins.coralogix.wordcount.endpoints.WordCountEndpoint
import mpurins.coralogix.wordcount.io.WordEventSource
import mpurins.coralogix.wordcount.models.Window
import mpurins.coralogix.wordcount.services._
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger

final case class Resources[F[_]](
    httpServer: Resource[F, Server],
    wordCountProcessor: Resource[F, Unit]
)

object Resources {

  def make[F[_]: Async](config: Config): F[Resources[F]] =
    (
      Observer.make[F, Window[Map[(String, String), Long]]],
      Slf4jLogger.create[F]
    ).mapN { (observer, logger) =>
      val wordCountProcessor = WordCountProcessor.resource(
        source = WordEventSource(config.source, logger),
        counterConfig = config.counter,
        observer = observer,
        logger = logger
      )

      val wordCountEndpoint = WordCountEndpoint(
        service = WordCountService(observer)
      )

      val httpServer = HttpServer(
        config = config.http,
        endpoints = List(wordCountEndpoint)
      )

      Resources(
        httpServer = httpServer,
        wordCountProcessor = wordCountProcessor
      )
    }
}
