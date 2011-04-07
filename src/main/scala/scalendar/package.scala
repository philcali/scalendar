package com.github.philcali
package object scalendar {

  import java.util.{Date, Calendar}
  import conversions.FromConversion

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

  // Use these only when necessary
  implicit def day2Int(day: Day.Value) = day.id
  implicit def month2Int(month: Month.Value) = month.id

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
}
