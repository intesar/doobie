package doobie.example

import doobie.util.transactor._
import doobie.contrib.specs2.AnalysisSpec

import org.specs2.mutable.Specification

import scalaz.concurrent.Task

object AnalysisTestSpec extends Specification with AnalysisSpec {
  val transactor = DriverManagerTransactor[Task]("org.postgresql.Driver", "jdbc:postgresql:world", "rnorris", "")
  check(AnalysisTest.speakerQuery(null, 0))
  check(AnalysisTest.speakerQuery2)
  check(AnalysisTest.update("foo", 42))
  check(AnalysisTest.update2)
}

