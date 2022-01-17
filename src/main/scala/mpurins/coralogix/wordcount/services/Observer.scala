package mpurins.coralogix.wordcount.services

import cats.Functor
import cats.effect.kernel.Ref
import cats.syntax.functor._
import cats.syntax.option._

trait Observer[F[_], Data] {
  def current: F[Option[Data]]
  def set(data: Data): F[Unit]
}

object Observer {
  def apply[F[_], Data](ref: Ref[F, Option[Data]]): Observer[F, Data] =
    new Observer[F, Data] {
      override def current: F[Option[Data]] = ref.get
      override def set(data: Data): F[Unit] = ref.set(data.some)
    }

  def make[F[_]: Functor: Ref.Make, Data]: F[Observer[F, Data]] =
    Ref[F].of(none[Data]).map(apply)
}
