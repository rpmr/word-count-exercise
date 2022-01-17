package mpurins.coralogix.wordcount

import cats.effect.{IO, IOApp}
import mpurins.coralogix.wordcount.app.App

object Main extends IOApp.Simple {
  override def run: IO[Unit] = App.run[IO]
}
