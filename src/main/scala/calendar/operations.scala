package com.github.philcali.calendar
package operations

import java.util.Calendar
import Calendar._

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
