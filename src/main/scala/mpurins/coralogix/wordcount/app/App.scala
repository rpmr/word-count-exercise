package mpurins.coralogix.wordcount.app

import cats.effect.kernel.Async
import cats.syntax.apply._
import cats.syntax.flatMap._
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax._

object App {
  def run[F[_]: Async]: F[Nothing] =
    ConfigSource.default
      .at("app")
      .loadF[F, Config]()
      .flatMap(Resources.make[F])
      .flatMap[Nothing] { resources =>
        (
          resources.wordCountProcessor,
          resources.httpServer
        ).tupled.useForever
      }
}
