import sbt._

object Deps {

  object Cats {
    val version: String  = "2.7.0"
    val core: ModuleID   = "org.typelevel" %% "cats-core"   % version
    val kernel: ModuleID = "org.typelevel" %% "cats-kernel" % version

    def all: Vector[ModuleID] = Vector(core, kernel)
  }

  object CatsEffect {
    val version: String  = "3.3.4"
    val core: ModuleID   = "org.typelevel" %% "cats-effect"        % version
    val kernel: ModuleID = "org.typelevel" %% "cats-effect-kernel" % version

    def all: Vector[ModuleID] = Vector(core, kernel)
  }

  object Circe {
    val version: String            = "0.14.1"
    val core: ModuleID             = "io.circe" %% "circe-core"           % version
    val generic: ModuleID          = "io.circe" %% "circe-generic"        % version
    val `generic-extras`: ModuleID = "io.circe" %% "circe-generic-extras" % version
    val jawn: ModuleID             = "io.circe" %% "circe-jawn"           % version

    def all: Vector[ModuleID] = Vector(core, generic, `generic-extras`, jawn)
  }

  object Fs2 {
    val version: String = "3.2.4"
    val core: ModuleID  = "co.fs2" %% "fs2-core" % version
    val io: ModuleID    = "co.fs2" %% "fs2-io"   % version

    def all: Vector[ModuleID] = Vector(core, io)
  }

  object Http4s {
    val version: String  = "0.23.7"
    val core: ModuleID   = "org.http4s" %% "http4s-core"         % version
    val server: ModuleID = "org.http4s" %% "http4s-server"       % version
    val ember: ModuleID  = "org.http4s" %% "http4s-ember-server" % version

    def all: Vector[ModuleID] = Vector(core, server, ember)
  }

  object Log4cats {
    val version: String = "2.1.1"
    val core: ModuleID  = "org.typelevel" %% "log4cats-core"  % version
    val slf4j: ModuleID = "org.typelevel" %% "log4cats-slf4j" % version

    def all: Vector[ModuleID] = Vector(core, slf4j)
  }

  object Other {
    val `cats-time`: ModuleID = "org.typelevel"  %% "cats-time"       % "0.5.0"
    val ip4s: ModuleID        = "com.comcast"    %% "ip4s-core"       % "3.1.2"
    val kittens: ModuleID     = "org.typelevel"  %% "kittens"         % "2.3.2"
    val logback: ModuleID     = "ch.qos.logback"  % "logback-classic" % "1.2.10" % Runtime
    val magnolia: ModuleID    = "com.propensive" %% "magnolia"        % "0.17.0"
    val shapeless: ModuleID   = "com.chuusai"    %% "shapeless"       % "2.3.7"
    val slf4j: ModuleID       = "org.slf4j"       % "slf4j-api"       % "1.7.33" % Runtime
    val `sttp-fs2`: ModuleID = "com.softwaremill.sttp.shared" %% "fs2" % "1.3.1"

    def all: Vector[ModuleID] =
      Vector(`cats-time`, ip4s, kittens, logback, magnolia, shapeless, slf4j, `sttp-fs2`)

  }

  object Pureconfig {
    val version: String          = "0.17.1"
    val core: ModuleID           = "com.github.pureconfig" %% "pureconfig-core"         % version
    val generic: ModuleID        = "com.github.pureconfig" %% "pureconfig-generic"      % version
    val `generic-base`: ModuleID = "com.github.pureconfig" %% "pureconfig-generic-base" % version
    val `cats-effect`: ModuleID  = "com.github.pureconfig" %% "pureconfig-cats-effect"  % version
    val ip4s: ModuleID           = "com.github.pureconfig" %% "pureconfig-ip4s"         % version

    def all: Vector[ModuleID] = Vector(core, generic, `generic-base`, `cats-effect`, ip4s)
  }

  object Tapir {
    val version: String  = "0.19.3"
    val core: ModuleID   = "com.softwaremill.sttp.tapir" %% "tapir-core"          % version
    val http4s: ModuleID = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % version
    val circe: ModuleID  = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % version

    def all: Vector[ModuleID] = Vector(core, http4s, circe)
  }

  object Testing {
    val munitVersion: String         = "0.7.29"
    val munit: ModuleID              = "org.scalameta" %% "munit"            % munitVersion
    val `munit-scalacheck`: ModuleID = "org.scalameta" %% "munit-scalacheck" % munitVersion

    val `munit-ce3`: ModuleID = "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"

    val scalacheckEffectVersion: String = "1.0.3"
    val `scalacheck-effect`: ModuleID =
      "org.typelevel" %% "scalacheck-effect" % scalacheckEffectVersion
    val `scalacheck-effect-munit`: ModuleID =
      "org.typelevel" %% "scalacheck-effect-munit" % scalacheckEffectVersion
    val `http4s-circe`: ModuleID = "org.http4s" %% "http4s-circe" % Http4s.version

    def all: Vector[ModuleID] = Vector(
      munit,
      `munit-scalacheck`,
      `munit-ce3`,
      `scalacheck-effect`,
      `scalacheck-effect-munit`,
      `http4s-circe`
    ).map(_ % Test)
  }
}
