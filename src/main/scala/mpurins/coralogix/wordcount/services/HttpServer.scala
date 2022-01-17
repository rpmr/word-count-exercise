package mpurins.coralogix.wordcount.services

import cats.effect.kernel.{Async, Resource}
import com.comcast.ip4s.{Host, Port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s._

object HttpServer {

  final case class Config(
      host: Option[Host],
      port: Port
  )

  def apply[F[_]: Async](
      config: Config,
      endpoints: List[ServerEndpoint[Any, F]]
  ): Resource[F, Server] = {
    val routes = Http4sServerInterpreter[F]().toRoutes(endpoints)

    EmberServerBuilder.default
      .withHttpApp(routes.orNotFound)
      .withPort(config.port)
      .withHostOption(config.host)
      .build
  }
}
