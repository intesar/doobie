package doobie.util

import scalaz._
import scalaz.concurrent._
import Scalaz._
import doobie.syntax.catchsql._
import doobie.enum.sqlstate._
import org.specs2.mutable.Specification
import java.sql.SQLException

object catchsqlpec extends Specification {

  val SQLSTATE_FOO = SqlState("Foo")
  val SQLSTATE_BAR = SqlState("Bar")

  "attemptSql" should {

    "do nothing on success" in {
      Task.delay(3).attemptSql.run must_== \/-(3)
    }

    "catch SQLException" in {
      val e = new SQLException
      Task.delay(throw e).attemptSql.run must_== -\/(e)
    }

    "ignore non-SQLException" in {      
      val e = new IllegalArgumentException
      Task.delay(throw e).attemptSql.run must throwA[IllegalArgumentException]
    }

  }


  "attemptSqlState" should {

    "do nothing on success" in {
      Task.delay(3).attemptSqlState.run must_== \/-(3)
    }

    "catch SQLException" in {
      val e = new SQLException("", SQLSTATE_FOO.value)
      Task.delay(throw e).attemptSqlState.run must_== -\/(SQLSTATE_FOO)
    }

    "ignore non-SQLException" in {      
      val e = new IllegalArgumentException
      Task.delay(throw e).attemptSqlState.run must throwA[IllegalArgumentException]
    }

  }
  
  "attemptSomeSqlState" should {

    "do nothing on success" in {
      Task.delay(3).attemptSomeSqlState {
        case SQLSTATE_FOO => 42
        case SQLSTATE_BAR        => 66
      }.run must_== \/-(3)
    }

    "catch SQLException with matching state (1)" in {
      val e = new SQLException("", SQLSTATE_FOO.value)
      Task.delay(throw e).attemptSomeSqlState {
        case SQLSTATE_FOO => 42
        case SQLSTATE_BAR        => 66
      }.run must_== -\/(42)
    }

    "catch SQLException with matching state (2)" in {
      val e = new SQLException("", SQLSTATE_BAR.value)
      Task.delay(throw e).attemptSomeSqlState {
        case SQLSTATE_FOO => 42
        case SQLSTATE_BAR        => 66
      }.run must_== -\/(66)
    }

    "ignore SQLException with non-matching state" in {
      val e = new SQLException("", SQLSTATE_BAR.value)
      Task.delay(throw e).attemptSomeSqlState {
        case SQLSTATE_FOO => 42
      }.run must throwA[SQLException]
    }

    "ignore non-SQLException" in {
      val e = new IllegalArgumentException
      Task.delay(throw e).attemptSomeSqlState {
        case SQLSTATE_FOO => 42
      }.run must throwA[IllegalArgumentException]
    }

  }

  "exceptSql" should {
    "..." in pending
  }

  "exceptSqlState" should {
    "..." in pending
  }

  "exceptSomeSqlState" should {
    "..." in pending
  }

  "exceptSql" should {
    "..." in pending
  }

  "onSqlException" should {
    "..." in pending
  }

}




