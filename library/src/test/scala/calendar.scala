package com.github.philcali.scalendar.test

import com.github.philcali.scalendar._
import conversions._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import java.util.Calendar
import java.util.Calendar._

class CalendarSpec extends FlatSpec with ShouldMatchers {
  val stuck = {
    val cal = Calendar.getInstance
    // February 1, 2011
    cal.set(2011, FEBRUARY, 1)
    cal
  }

  implicit val pattern = Pattern("M/d/yyyy")

  val test = new Scalendar(stuck.getTimeInMillis)

  "Internal Calendar" should "pull the right month" in {
    test.month.name should be === "February"
  } 

  it should "produce a duration from dsl" in {
    val duration = Scalendar(2011, 2, 1) to Scalendar(2011, 2, 5) 

    duration.delta.days should be === 4
    duration.delta.hours should be === (4 * 24)
  }

  it should "be able to handle date arithmetic" in {
    val dayLater = test + Days(1) 
    
    dayLater.day.value should be === 2
  }

  it should "be able to handle correct arthimetic" in {
    val test = Scalendar(2011, 2, 1)

    val april = test + Months(2) 

    val yearfrom = test + Months(12) 

    (test + Months(1)).day.value should be === 1
    (test + Weeks(1)).day.value should be === 8
    (test + Weeks(1)).day.name should be === test.day.name
    (test + Days(3)).day.value should be === 4
    april.day.value should be === 1
    april.month.name should be === "April"
    yearfrom.month.name should be === test.month.name
    yearfrom.day.value should be === test.day.value
  }

  it should "be able to form a duration" in {
    val newDuration = test to Scalendar(2011, 3, 19) to Scalendar(2011, 4, 21)

    newDuration.end.month.name should be === "April"
  }

  it should "be a traversable duration" in {
    val duration = Scalendar(2011, 2, 1) to (Scalendar(2011, 3, 1) - Milliseconds(1))
  
    duration.start.day.value should be === 1
    duration.end.day.value should be === 28
 
    duration.traverse(Weeks(1)) { weekDuration =>
      weekDuration.traverse(Days(1)) { dayDuration =>
        dayDuration.delta.hours should be === 23 
      }
    }
  }

  it should "produce a calendar month fairly easily" in {
    val month = Scalendar(2011, 2, 14).calendarMonth
 
    month.by(Weeks(1)).foldLeft(0) {(a, b) => a + 1 } should be === 5
  }

  it should "filter time just as easy as a list" in {
    // Pure Scala
    Scalendar(2011, 2, 1).calendarMonth.by(Days(1)) filter(_.day.name match {
      case "Monday" | "Wednesday" | "Friday" => true
      case _ => false
    }) foreach { mwf =>
      mwf.isWeekday should be === true
    }
  }

  it should "be able contained within a duration" in {
    val twoweeks = Scalendar(2011, 2, 1) to Scalendar(2011, 2, 15)
    Scalendar(2011, 2, 6) isIn twoweeks should be === true
    Scalendar(2011, 3, 1) isIn twoweeks should be === false
  }

  it should "test for equality" in {
    Scalendar(2011, 2, 1) should be === Scalendar(2011, 2, 1) 
    Scalendar(2011, 2, 5) < Scalendar(2011, 2, 6) should be === true
    Scalendar(1999, 12, 16) < Scalendar(2011, 3, 10) should be === true
  }

  it should "perform all kinds of calendar operations" in {
    val stuck = Scalendar(2011, 2, 7) 

    stuck.day.value should be === 7
    stuck.day.name should be === "Monday"
    stuck.isWeekday should be === true
    stuck.month.name should be === "February"
  }

  it should "be reversable" in {
    val negspan = Scalendar(2011, 2, 28) to Scalendar(2011, 1, 31) 
    
    negspan.delta.days should be === -28

    val countdown = negspan.by(Days(1)).map(_.day.value)
    countdown.take(5).mkString(",") should be === "28,27,26,25,24"
  }

  it should "be able to create via 'setters'" in {
    val now = Scalendar.now 

    val newTime = now.year(2011).month(3).day(2)
    val anotherTime = Scalendar(2011, 3, 2)

    newTime.day.value should be === 2
    newTime.month.name should be === "March" 
    newTime.year.value should be === 2011
    anotherTime.year should be === newTime.year
    anotherTime.month should be === newTime.month
    anotherTime.day should be === newTime.day
  }

  it should "be able specialized setters" in {
    val time = Scalendar(2011, 4, 3).hour(13)
    val expected1 = Scalendar(2011, 4, 3)
    val expected2 = expected1.hour(23).minute(59).second(59) 

    val expected3 = expected2.day.inWeek(SATURDAY)

    Scalendar.beginDay(time) should be === expected1
    Scalendar.endDay(time) should be === expected2
    Scalendar.endWeek(time) should be === expected3
  }

  "Durations" should "be able to create nifty UI elements" in {
    val html = 
<table>{
  test.calendarMonth.traverse(Weeks(1)) { weekD =>
    <tr>{
      weekD.traverse(Days(1)) { dayD =>
        <td>{ dayD.day.value.toString }</td>
      }
    }</tr>
  }
}</table>

    val testDays = ((html \\ "tr")(1) \ "td").map(_.text)

    (html \\ "tr").size should be === 5
    (html \\ "td").size should be === 35
    testDays.mkString(",") should be === "6,7,8,9,10,11,12"
  }

  "TimeZones" should "be settable like any other calendar field" in {
    import java.util.TimeZone
    
    val indian = Scalendar.now.tz("Indian/Chagos")
    val cst = Scalendar.now.tz("CST")

    val indiantz = TimeZone.getTimeZone("Indian/Chagos")
    val csttz = TimeZone.getTimeZone("CST")
      
    // Offsets from UTC
    indian.tz.offset should be === indiantz.getRawOffset 
    cst.tz.offset should be === csttz.getRawOffset

    cst.tz.offset(indian) should be === 12 * 1000 * 60 * 60
  }

  "Periods" should "be created from adding fields together" in {
    val period = Days(4) + Hours(3) - Minutes(2)
    
    val expected = (4 * 24 * 60 * 60 * 1000) + 
                   (3 * 60 * 60 * 1000) - 
                   (2 * 60 * 1000)

    period.milliseconds should be === expected
    Hours(3).milliseconds should be === (3 * 60 * 60 * 1000)
  }

  it should "be able to transform it's output" in {
    Hours(3).into.minutes should be === 180 
    Days(4).into.hours should be === (4 * 24) 
  }

  it should "be able to add to times" in {
    val twoweeks = Weeks(1) + Days(7) 
    val april = Scalendar(2011, 4, 1)
    val expected = april + Weeks(2) 

    (april + twoweeks) should be === expected
  }
}
