package com.github.philcali

// The preferred way to import implicits.
// In Scala 2.8 +
package object scalendar {
  implicit def number2Conversion(num: Int) = 
    Imports.number2Conversion(num)
  implicit def fromString(dateString: String)
                         (implicit pattern: java.text.SimpleDateFormat) = 
    Imports.fromString(dateString)(pattern)

  implicit def fromDate(date: java.util.Date) =
    Imports.fromDate(date)

  implicit def fromCalendar(cal: java.util.Calendar) =
    Imports.fromCalendar(cal)

  implicit def toDate(cal: Scalendar) =
    Imports.toDate(cal) 
  implicit def toCalendar(cal: Scalendar) = 
    Imports.toCalendar(cal)

  implicit def day2Int(day: Day.Value) = Imports.day2Int(day)
  implicit def month2Int(day: Month.Value) = Imports.month2Int(day)
}
