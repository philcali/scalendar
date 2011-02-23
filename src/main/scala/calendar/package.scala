package com.philipcali.utils
package object calendar {

  import java.util.Calendar

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

  implicit def toDate(cal: Scalendar) = 
    new java.util.Date(cal.time)

  implicit def toCalendar(cal: Scalendar): Calendar = {
    val time = Calendar.getInstance
    time.setTimeInMillis(cal.time)
    time
  }

}
