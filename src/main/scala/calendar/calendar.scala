package com.github.philcali
package calendar 

import conversions._
import operations.RichSupport

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

  def copy = new Scalendar(time)

  def +(eval: Evaluated) = {
    val newTime = Calendar.getInstance
    newTime.setTimeInMillis(time)
    newTime.add(eval.field, eval.number)
    
    val diff = newTime.getTimeInMillis - time
    
    new Scalendar(time + diff)
  }

  def -(eval: Evaluated) = this + Evaluated(eval.field, -1 * eval.number)

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
