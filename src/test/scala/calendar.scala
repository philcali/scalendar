package com.github.philcali
package test

import scalendar._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import java.util.Calendar
import java.util.Calendar._

class CalendarSpec extends FlatSpec with ShouldMatchers {
  val stuck = {
    val cal = Calendar.getInstance
    // February 1, 2011
    cal.set(2011, 1, 1)
    cal
  }

  implicit val pattern = Pattern("M/d/yyyy")

  val test = new Scalendar(stuck.getTimeInMillis)

  "Internal Calendar" should "pull the right month" in {
    test.month.name should be === "February"
  } 

  it should "produce a duration from dsl" in {
    val duration = "02/01/2011" to "02/05/2011"

    duration.delta.days should be === 4
    duration.delta.hours should be === (4 * 24)
  }

  it should "be able to handle date arithmetic" in {
    val dayLater = test + (1 day)
    
    dayLater.day.value should be === 2
  }

  it should "be able to handle correct arthimetic" in {
    val test = Scalendar(year = 2011, month = FEBRUARY, day=1)

    val april = test + (2 months)

    val yearfrom = test + (12 months)

    (test + (1 month)).day.value should be === 1
    (test + (1 week)).day.value should be === 8
    (test + (1 week)).day.name should be === test.day.name
    (test + (3 days)).day.value should be === 4
    april.day.value should be === 1
    april.month.name should be === "April"
    yearfrom.month.name should be === test.month.name
    yearfrom.day.value should be === test.day.value
  }

  it should "be able to form a duration" in {
    val newDuration = test to "03/19/2011" to "04/21/2011" 

    newDuration.end.month.name should be === "April"
  }

  it should "be a traversable duration" in {
    val duration = "02/01/2011" to ("03/01/2011" - (1 millisecond))
  
    duration.start.day.value should be === 1
    duration.end.day.value should be === 28
 
    duration.traverse(1 week) { weekDuration =>
      weekDuration.traverse(1 day) { dayDuration =>
        dayDuration.delta.hours should be === 23 
      }
    }
  }

  it should "produce a calendar month fairly easily" in {
    val month = "2/14/2011".calendarMonth
 
    month.by(1 week).foldLeft(0) {(a, b) => a + 1 } should be === 5
  }

  it should "filter time just as easy as a list" in {
    // Pure Scala
    "2/1/2011".calendarMonth.by(1 day) filter(_.day.name match {
      case "Monday" | "Wednesday" | "Friday" => true
      case _ => false
    }) foreach { mwf =>
      mwf.isWeekday should be === true
    }
  }

  it should "be able contained within a duration" in {
    "2/6/2011" isIn ("2/1/2011" to "2/15/2011") should be === true
    "3/1/2011" isIn ("2/1/2011" to "2/15/2011") should be === false
  }

  it should "test for equality" in {
    fromString("2/1/2011") should be === fromString("2/1/2011")
    "2/5/2011".time < "2/6/2011".time should be === true
    "12/5/2009".time < "2/6/2011".time should be === true
  }

  it should "perform all kinds of calendar operations" in {
    val stuck = fromString("2/7/2011")

    stuck.day.value should be === 7
    stuck.day.name should be === "Monday"
    stuck.isWeekday should be === true
    stuck.month.name should be === "February"
  }

  it should "be reversable" in {
    val negspan = "2/28/2011" to "1/31/2011"
    
    negspan.delta.days should be === -28

    val countdown = negspan.by(1 day).map(_.day.value)
    countdown.take(5).mkString(",") should be === "28,27,26,25,24"
  }

  it should "be able to create via 'setters'" in {
    val now = Scalendar.now 

    val newTime = now.year(2011).month(MARCH).day(2)
    val anotherTime = Scalendar(year = 2011, month = MARCH, day = 2)

    newTime.day.value should be === 2
    newTime.month.name should be === "March" 
    newTime.year.value should be === 2011
    anotherTime.year should be === newTime.year
    anotherTime.month should be === newTime.month
    anotherTime.day should be === newTime.day
  }

  "Durations" should "be able to create nifty UI elements" in {
    val html = 
<table>{
  test.calendarMonth.traverse(1 week) { weekD =>
    <tr>{
      weekD.traverse(1 day) { dayD =>
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
}
