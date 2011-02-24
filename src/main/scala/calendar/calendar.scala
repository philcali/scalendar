package com.philipcali.utils
package calendar 

import conversions._
import java.util.Calendar
import Calendar._

object Pattern {
  def apply(pattern: String) = 
    new java.text.SimpleDateFormat(pattern)
  def unapply(formatter: java.text.SimpleDateFormat) =
    Some(formatter.toPattern)
}

object Scalendar {
  def now = new Scalendar() 

  def apply(millis: Long) = new Scalendar(millis)
 
  def apply(millisecond: Int = 0, second: Int = 0, minute: Int = 0,
            hour: Int = 0, day: Int = 0, month: Int = -1, year: Int = 0) = {
    val start = new Scalendar() 
    val workingYear = if(year <= 0) start.year.value else year
    val workingMonth = if(month <= -1) start.month.value else month
    val workingDay = if(day <= 0) start.day.value else day

    start.year(workingYear)
         .month(workingMonth)
         .day(workingDay)
         .hour(hour)
         .minute(minute)
         .second(second)
         .millisecond(millisecond)
  }

  def dayOfWeek(day: Int) = day match {
    case SUNDAY => "Sunday"
    case MONDAY => "Monday"
    case TUESDAY => "Tuesday"
    case WEDNESDAY => "Wednesday"
    case THURSDAY => "Thusday"
    case FRIDAY => "Friday"
    case SATURDAY => "Saturday"
  }
  
  def monthName(month: Int) = month match {
    case JANUARY => "January"
    case FEBRUARY => "February"
    case MARCH => "March"
    case APRIL => "April"
    case MAY => "May"
    case JUNE => "June"
    case JULY => "July"
    case AUGUST => "August"
    case SEPTEMBER => "September"
    case OCTOBER => "October"
    case NOVEMBER => "November"
    case DECEMBER => "December"
  }

  def daynames = (SUNDAY to SATURDAY) map(day => dayOfWeek(day).substring(0, 3))

  def zeroOut(cal: Scalendar) = {
    cal.hour(0).minute(0).second(0).millisecond(0)
  }

  def endDay(cal: Scalendar) = {
    cal.hour(23).minute(59).second(59)
  }

  def beginWeek(cal: Scalendar) = {
    cal.set(DAY_OF_WEEK, SUNDAY)
  }

  def endWeek(cal: Scalendar) = {
    endDay(cal.set(DAY_OF_WEEK, SATURDAY))
  }
}

class Duration(from: Long, last: Long) extends RichSupport {
  val start = new Scalendar(from)
  val end = new Scalendar(last)

  // This gives a duration rather strong support for
  // pulling calendar values
  protected val javaTime: Calendar = toCalendar(start)

  def delta = new ToConversion(end.time - start.time)

  def to(spot: Scalendar) = 
    new Duration(start.time, spot.time)

  def to(duration: Duration) =
    new Duration(start.time, duration.end.time)

  def + (duration: Duration) =
    new Duration(start.time, end.time + duration.delta.milliseconds)

  def - (duration: Duration) =
    new Duration(start.time, end.time - duration.delta.milliseconds)

  def traverse[A](value: Long)(fun: Duration => A) = {
    val (max, min) = if(delta.milliseconds < 0) {
      (0, (delta.milliseconds / value).toInt) 
    } else {
      ((delta.milliseconds / value).toInt, 0)
    }
    val mult = (num: Long) => if(num < 1) -1 * num else num

    for(iter <- (min to max);
        val dur = new Duration(start.time + mult(value * iter), 
                               end.time - mult(value * (max - iter))))
    yield(fun(dur))
  }

  def contains(cal: Scalendar) = cal isIn this
  def contains(time: Long) = Scalendar(time) isIn this 

  def reverse = new Duration(end.time, start.time)

  def by(value: Long): List[Duration] = {
    traverse(value)(dur => dur).toList
  }

  def next(value: Long) = {
    val incrementer = if(delta.milliseconds < 0) -1 else 1
    if(value > delta.milliseconds * incrementer) {
      new Duration(start.time + delta.milliseconds, end.time)
    } else {
      new Duration(start.time + value * incrementer, end.time)
    }
  }

  override def toString = "%s - %s" format(start, end)
}

object CalendarDayDuration {
  import Scalendar._

  def apply(cal: Scalendar) = {
    zeroOut(cal) to endDay(cal)
  }
}

object CalendarWeekDuration {
  import Scalendar._

  def apply(cal: Scalendar) = {
    beginWeek(zeroOut(cal)) to endWeek(endDay(cal))
  }
}

object CalendarMonthDuration {
  import Scalendar._

  def apply(cal: Scalendar) = {
    val nextMonth = cal.day(1) + (1 month) - (1 day)

    beginWeek(zeroOut(cal.day(1))) to
    endWeek(zeroOut(nextMonth))
  }
}

trait CalendarOperations {
  protected val javaTime: Calendar
  
  def millisecond(t: Int) = set(MILLISECOND, t)
  def millisecond = javaTime.get(MILLISECOND)

  def copyTime = {
    val copied = Calendar.getInstance
    copied.setTimeInMillis(javaTime.getTimeInMillis)
    copied
  }

  def set(typ: Int, value: Int) = {
    val copied = copyTime
    copied.set(typ, value)
    new Scalendar(copied.getTimeInMillis)
  }
}

abstract class CalendarField {
  val value: Int
  def name: String
  def is(field: Int) = value == field
  override def equals(other: Any) = other match {
    case field: CalendarField => this.value == field.value
    case field: Int => this.value == field
    case _ => false
  }
  override def toString = name
}

trait SecondFieldOperations extends CalendarOperations {
  def second(t: Int) = set(SECOND, t)

  def second = new CalendarField {
    val value = javaTime.get(SECOND)
    def name = value.toString
  }
}

trait MinuteFieldOperations extends CalendarOperations {
  def minute(t: Int) = set(MINUTE, t)

  def minute = new CalendarField {
    val value = javaTime.get(MINUTE)
    def name = "%d:00" format(value)
  }
}

trait HourFieldOperations extends CalendarOperations {
  def hour(t: Int) = set(HOUR, t)

  def hour = new CalendarField {
    val value = javaTime.get(HOUR)
    def name = "%d:00:00" format(value) 
  }
}

trait DailyOperations extends CalendarOperations {
  def inWeek = javaTime.get(DAY_OF_WEEK)
  def isWeekend = inWeek == SUNDAY || inWeek == SATURDAY
  def isWeekday = !isWeekend
}

trait DayFieldOperations extends DailyOperations { outer =>
  def day = new DayField 

  def day(t: Int) = set(DATE, t)

  class DayField extends CalendarField with DailyOperations {
    val javaTime = outer.javaTime
    def name = Scalendar.dayOfWeek(inWeek)
    def inYear = javaTime.get(DAY_OF_YEAR)
    val value = javaTime.get(DATE)
    override def toString = "%s the %d" format(name, value)
  }
}

trait WeekFieldOperations extends CalendarOperations {
  def week(t: Int) = set(WEEK_OF_MONTH, t)

  def week = new WeekField

  class WeekField extends CalendarField {
    val value = javaTime.get(WEEK_OF_MONTH)
    def inYear = javaTime.get(WEEK_OF_YEAR)
    def name = value match {
      case 1 => "first"
      case 2 => "second"
      case 3 => "third"
      case 4 => "forth"
      case 5 => "fifth"
    }
  }
}

trait MonthFieldOperations extends CalendarOperations {
  def month(t: Int) = set(MONTH, t)

  def month = new CalendarField {
    val value = javaTime.get(MONTH)
    def name = Scalendar.monthName(value)
  }
}

trait YearFieldOperations extends CalendarOperations {
  def year(t: Int) = set(YEAR, t)

  def year = new CalendarField {
    val value = javaTime.get(YEAR)
    def name = "%d - %d" format(value, value + 1)
  }
}

trait RichSupport extends YearFieldOperations 
                     with MonthFieldOperations
                     with WeekFieldOperations
                     with DayFieldOperations
                     with HourFieldOperations
                     with MinuteFieldOperations
                     with SecondFieldOperations

class Scalendar(now: Long = System.currentTimeMillis) extends Ordered[Scalendar] 
                                                             with RichSupport {  
  import Scalendar._

  protected val javaTime = {
    val calendar = Calendar.getInstance()
    calendar.setTimeInMillis(now)
    calendar
  }

  def compare(that: Scalendar) = this.time compare that.time

  override def equals(something: Any) = something match {
    case cal: Scalendar => cal.time == this.time
    case millis: Long => millis == time
    case _ => false
  }

  def time = javaTime.getTimeInMillis 

  def +(millis: Long) = new Scalendar(time + millis)
  def -(millis: Long) = new Scalendar(time - millis)

  def isIn(duration: Duration) = 
    time >= duration.start.time && time <= duration.end.time

  def to(to: Scalendar) = 
    new Duration(time, to.time)

  def to(to: Long) = 
    new Duration(time, to)

  def calendarMonth = CalendarMonthDuration(this) 
  
  def calendarWeek = CalendarWeekDuration(this) 

  def calendarDay = CalendarDayDuration(this) 

  override def toString = Pattern("MM/dd/yyyy HH:mm:ss").format(javaTime.getTime)
}
