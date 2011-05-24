package com.github.philcali

package object scalendar {

  import java.util.{Date, Calendar}
  import conversions._

  implicit def number2Conversion(num: Int) = 
    new FromConversion(num)

  implicit def fromString(dateString: String)
                         (implicit pattern: java.text.SimpleDateFormat) = {
      val time = pattern.parse(dateString)
      new Scalendar(time.getTime)
  }

  implicit def fromDate(date: Date) = 
    new Scalendar(date.getTime)

  implicit def fromCalendar(cal: Calendar) = 
    new Scalendar(cal.getTimeInMillis)

  implicit def toDate(cal: Scalendar): Date = 
    new Date(cal.time)

  implicit def toCalendar(cal: Scalendar): Calendar = {
    val newtime = Calendar.getInstance
    newtime.setTimeInMillis(cal.time)
    newtime
  }

  implicit def evalToPeriod(evaluated: Evaluated) = {
    Period(evaluated :: Nil)
  }

  // Use these only when necessary
  implicit def day2Int(day: Day.Value) = day.id
  implicit def month2Int(month: Month.Value) = month.id
}
