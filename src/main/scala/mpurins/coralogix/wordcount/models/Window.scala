package mpurins.coralogix.wordcount.models

import cats.derived.semiauto
import cats.kernel.Eq
import org.typelevel.cats.time._

import java.time.Instant

final case class Window[Data](
    opensWhen: Instant,
    closesWhen: Instant,
    data: Data
)

object Window {
  implicit def eq[Data: Eq]: Eq[Window[Data]] = semiauto.eq[Window[Data]]
}
