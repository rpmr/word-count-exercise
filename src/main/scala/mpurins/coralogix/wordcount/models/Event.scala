package mpurins.coralogix.wordcount.models

import java.time.Instant

final case class Event(
    eventType: String,
    data: String,
    timestamp: Instant
)
