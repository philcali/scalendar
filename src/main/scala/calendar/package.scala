package com.philipcali.utils
package object calendar {

  import java.util.{Date, Calendar}
  import conversions.FromConversion

  implicit def number2Conversion(num: Long) = 
    new FromConversion(num.toLong)

  implicit def fromString(dateString: String)
                         (implicit pattern: java.text.SimpleDateFormat) = {
      val time = pattern.parse(dateString)
      new Scalendar(time.getTime)
  }

  implicit def fromDate(date: java.util.Date) = 
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

}
