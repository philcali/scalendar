package com.github.philcali.scalendar

import conversions._
import operations.RichSupport

import java.util.{Date, Calendar}

object Month extends Enumeration(1) {
  type Month = Value
  val January = Value("January")
  val February = Value("February")
  val March = Value("March")
  val April = Value("April")
  val May = Value("May")
  val June = Value("June")
  val July = Value("July")
  val August = Value("August")
  val September = Value("September") 
  val October = Value("October")
  val November = Value("November")
  val December = Value("December")
}

object Day extends Enumeration(1) {
  type Day = Value
  val Sunday = Value("Sunday")
  val Monday = Value("Monday")
  val Tuesday= Value("Tuesday")
  val Wednesday = Value("Wednesday")
  val Thursday = Value("Thursday")
  val Friday = Value("Friday")
  val Saturday = Value("Saturday")
}

object Pattern {
  def apply(pattern: String) = 
    new java.text.SimpleDateFormat(pattern)
  def unapply(formatter: java.text.SimpleDateFormat) =
    Some(formatter.toPattern)
}

object Scalendar {
  def now = new Scalendar() 

  def apply(millis: Long) = new Scalendar(millis)

  def apply(year: Int, month: Int, day: Int) = 
    beginDay(now).year(year).month(month).day(day)

  def apply(year: Int, month: Int, day: Int, hour: Int,
            minute: Int, second: Int, millisecond: Int) = 
    now.year(year)
       .month(month)
       .day(day)
       .hour(hour)
       .minute(minute)
       .second(second)
       .millisecond(millisecond)
 
  def dayOfWeek(day: Int) = Day(day).toString
  
  def monthName(month: Int) = Month(month).toString

  def daynames = (1 to 7).map (Day(_).toString.substring(0,3))

  def beginDay(cal: Scalendar) = {
    cal.hour(0).minute(0).second(0).millisecond(0)
  }

  def endDay(cal: Scalendar) = {
    cal.hour(23).minute(59).second(59)
  }

  def beginWeek(cal: Scalendar) = {
    beginDay(cal.inWeek(Day.Sunday))
  }

  def endWeek(cal: Scalendar) = {
    endDay(cal.inWeek(Day.Saturday))
  }
}

object CalendarDayDuration {
  import Scalendar._

  def apply(cal: Scalendar) = {
    beginDay(cal) to endDay(cal)
  }
}

object CalendarWeekDuration {
  import Scalendar._

  def apply(cal: Scalendar) = {
    beginWeek(cal) to endWeek(cal)
  }
}

object CalendarMonthDuration {
  import Scalendar._

  def apply(cal: Scalendar) = {
    val nextMonth = cal.day(1) + Months(1) - Days(1)

    beginWeek(cal.day(1)) to
    endWeek(nextMonth)
  }
}

class Scalendar(now: Long) extends Ordered[Scalendar] 
                              with RichSupport {  
  import Scalendar._

  def this() = this(System.currentTimeMillis)

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

  def copy = new Scalendar(time)

  def + (period: Period): Scalendar = period.fields.foldLeft (this) (_ + _)
  def - (period: Period): Scalendar = period.fields.foldLeft (this) (_ - _)

  def +(eval: Evaluated) = {
    val newTime = Calendar.getInstance
    newTime.setTimeInMillis(time)
    newTime.add(eval.field, eval.number)
    
    val diff = newTime.getTimeInMillis - time
    
    new Scalendar(time + diff)
  }

  def -(eval: Evaluated) = this + eval.negate 

  def isIn(duration: Duration) = 
    time >= duration.start.time && time <= duration.end.time

  def to(to: Scalendar) = 
    new Duration(time, to.time)

  def to(to: Long) = 
    new Duration(time, to)

  def calendarMonth = CalendarMonthDuration(this) 
  
  def calendarWeek = CalendarWeekDuration(this) 

  def calendarDay = CalendarDayDuration(this) 

  // Explicit conversions to java time
  def date = new java.util.Date(time)
  def cal = copyTime
  override def toString = Pattern("MM/dd/yyyy HH:mm:ss").format(javaTime.getTime)
}
