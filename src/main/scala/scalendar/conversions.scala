package scalendar
package conversions

import java.util.Calendar._

sealed trait PeriodBuilder {
  val fields: List[Evaluated]

  def + (other: Evaluated) = Period(other :: fields)
  def - (other: Evaluated) = Period(other.negate :: fields)
  def milliseconds = {
    val now = Scalendar.now
    val future = fields.foldLeft(now)(_ + _)
    (now to future).delta.milliseconds
  }
  def into = new ToConversion(this.milliseconds)
}

case class Period (fields: List[Evaluated]) extends PeriodBuilder

case class Evaluated(field: Int, number: Int) extends PeriodBuilder {
  val fields = List(this)
  def negate = Evaluated(field, -number)
}

sealed trait EvaluatedField {
  val field: Int
  def apply(number: Int) = Evaluated(field, number)
  def unapply(evald: Evaluated) = evald match {
    case Evaluated(f, n) if f == field => Some(n)
    case _ => None
  }
}

object Milliseconds extends EvaluatedField {
  val field = MILLISECOND
}
object Seconds extends EvaluatedField {
  val field = SECOND
}
object Minutes extends EvaluatedField {
  val field = MINUTE
}
object Hours extends EvaluatedField {
  val field = HOUR
}
object Days extends EvaluatedField {
  val field = DATE
}
object Weeks extends EvaluatedField {
  val field = WEEK_OF_MONTH
}
object Months extends EvaluatedField {
  val field = MONTH
}
object Years extends EvaluatedField {
  val field = YEAR
}

class FromConversion(number: Int) {
  def millisecond = Evaluated(MILLISECOND, number)
  def second = Evaluated(SECOND, number)
  def minute = Evaluated(MINUTE, number)
  def hour = Evaluated(HOUR, number)
  def day = Evaluated(DATE, number)
  def week = Evaluated(WEEK_OF_MONTH, number)
  def month = Evaluated(MONTH, number)
  def year = Evaluated(YEAR, number)

  def milliseconds = millisecond
  def seconds = second
  def minutes = minute
  def hours = hour
  def days = day
  def weeks = week
  def months = month
  def years = year
}

class ToConversion(number: Long) {
  def milliseconds = number
  def seconds = number / 1000
  def minutes = seconds / 60
  def hours = minutes / 60
  def days = hours / 24
  def weeks = days / 7
  def months  = days / 30
  def years = days / 365
}
