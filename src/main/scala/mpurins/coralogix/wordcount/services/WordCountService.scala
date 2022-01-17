package mpurins.coralogix.wordcount.services

import cats.Functor
import cats.syntax.functor._
import mpurins.coralogix.wordcount.models.Window

trait WordCountService[F[_]] {
  def current: F[Map[String, Map[String, Long]]]
}

object WordCountService {
  def apply[F[_]: Functor](
      observer: Observer[F, Window[Map[(String, String), Long]]]
  ): WordCountService[F] =
    new WordCountService[F] {
      override def current: F[Map[String, Map[String, Long]]] =
        observer.current.map(_.fold(Map.empty[(String, String), Long])(_.data).groupMapReduce {
          case ((eventType, _), _) => eventType
        } {
          case ((_, word), count) =>
            Map(word -> count)
        }(_ ++ _))
    }
}
