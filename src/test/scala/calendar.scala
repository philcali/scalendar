package scalendar
package test

import conversions._

import org.scalatest._

import java.util.Calendar
import java.util.Calendar._

// Test our Implicits too
class CalendarSpec extends FlatSpec with Matchers {
  val stuck = {
    val cal = Calendar.getInstance
    // February 1, 2011
    cal.set(2011, FEBRUARY, 1)
    cal
  }

  implicit val pattern = Pattern("M/d/yyyy")

  val test = Scalendar(stuck.getTimeInMillis)

  "Internal Calendar" should "pull the right month" in {
    test.month.name should === ("February")
  }

  it should "produce a duration from dsl" in {
    val duration = "2/1/2011" to Scalendar(2011, 2, 5) 

    duration.delta.days should === (4)
    duration.delta.hours should === (4 * 24)
  }

  it should "be able to handle date arithmetic" in {
    val dayLater = test + 1.day

    dayLater.day.value should === (2)
  }

  it should "be able to handle correct arthimetic" in {
    val test = Scalendar(2011, 2, 1)

    val april = test + 2.months

    val yearfrom = test + 12.months

    (test + Months(1)).day.value should === (1)
    (test + Weeks(1)).day.value should === (8)
    (test + Weeks(1)).day.name should === (test.day.name)
    (test + Days(3)).day.value should === (4)
    april.day.value should === (1)
    april.month.name should === ("April")
    yearfrom.month.name should === (test.month.name)
    yearfrom.day.value should === (test.day.value)
  }

  it should "be able to form a duration" in {
    val newDuration = test to "3/19/2011" to "4/12/2011"

    newDuration.end.month.name should === ("April")
  }

  it should "be a traversable duration" in {
    val duration = Scalendar(2011, 2, 1) to (Scalendar(2011, 3, 1) - Milliseconds(1))

    duration.start.day.value should === (1)
    duration.end.day.value should === (28)

    duration.traverse(Weeks(1)) { weekDuration =>
      weekDuration.traverse(Days(1)) { dayDuration =>
        dayDuration.delta.hours should === (23)
      }
    }
  }

  it should "produce a calendar month fairly easily" in {
    val month = Scalendar(2011, 2, 14).calendarMonth

    month.by(Weeks(1)).foldLeft(0) {(a, b) => a + 1 } should === (5)
  }

  it should "filter time just as easy as a list" in {
    // Pure Scala
    Scalendar(2011, 2, 1).calendarMonth.by(Days(1)) filter(_.day.name match {
      case "Monday" | "Wednesday" | "Friday" => true
      case _ => false
    }) foreach { mwf =>
      mwf.isWeekday should === (true)
    }
  }

  it should "be able contained within a duration" in {
    val twoweeks = Scalendar(2011, 2, 1) to Scalendar(2011, 2, 15)
    Scalendar(2011, 2, 6) isIn twoweeks should === (true)
    Scalendar(2011, 3, 1) isIn twoweeks should === (false)
  }

  it should "test for equality" in {
    Scalendar(2011, 2, 1) should === (Scalendar(2011, 2, 1))
    Scalendar(2011, 2, 5) < Scalendar(2011, 2, 6) should === (true)
    Scalendar(1999, 12, 16) < Scalendar(2011, 3, 10) should === (true)
  }

  it should "perform all kinds of calendar operations" in {
    val stuck = Scalendar(2011, 2, 7)

    stuck.day.value should === (7)
    stuck.day.name should === ("Monday")
    stuck.isWeekday should === (true)
    stuck.month.name should === ("February")
  }

  it should "be reversable" in {
    val negspan = Scalendar(2011, 2, 28) to Scalendar(2011, 1, 31)

    negspan.delta.days should === (-28)

    val countdown = negspan.by(Days(1)).map(_.day.value)
    countdown.take(5).mkString(",") should === ("28,27,26,25,24")
  }

  it should "be able to create via 'setters'" in {
    val now = Scalendar.now 

    val newTime = now.year(2011).month(3).day(2)
    val anotherTime = Scalendar(2011, 3, 2)

    newTime.day.value should === (2)
    newTime.month.name should === ("March")
    newTime.year.value should === (2011)
    anotherTime.year should === (newTime.year)
    anotherTime.month should === (newTime.month)
    anotherTime.day should === (newTime.day)
  }

  it should "be able specialized setters" in {
    val time = Scalendar(2011, 4, 3).hour(13)
    val expected1 = Scalendar(2011, 4, 3)
    val expected2 = expected1.hour(23).minute(59).second(59)

    val expected3 = expected2.day.inWeek(SATURDAY)

    Scalendar.beginDay(time) should === (expected1)
    Scalendar.endDay(time) should === (expected2)
    Scalendar.endWeek(time) should === (expected3)
  }

  it should "be completely immutable" in {
    val current = Scalendar(2011, 4, 20)
    val begin = current.day(1)

    val day = Day.Thursday.id

    val rtn = begin to (begin + 1.week) by 1.day find (_.inWeek == day) map { d =>
      (1 to 2).map(_ => d.start + 2.weeks).find(_ >= current) match {
        case Some(t) => begin
        case None => begin
      }
    } getOrElse begin

    begin should === (rtn)
  }

  it should "rolling the month should appropriate the days" in {
    val lastday = Scalendar(2011, 5, 31)

    val expected = Scalendar(2011, 6, 30)

    lastday + 1.month should === (expected)
  }

  it should "be able to produce month durations fairly easily" in {
    val june = Scalendar(2011, 6, 15)

    val days = june.month.duration by 1.day 
    days.size should === (30)
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

    (html \\ "tr").size should === (5)
    (html \\ "td").size should === (35)
    testDays.mkString(",") should === ("6,7,8,9,10,11,12")
  }

  it should "be able to filter easily" in {
    val june = Scalendar(2011, 6, 15).month.duration

    import Day.Monday

    val mondays = june occurrencesOf Monday

    mondays.size should === (4)
    mondays map (_.day.value) mkString(",") should === ("6,13,20,27")
  }

  "TimeZones" should "be settable like any other calendar field" in {
    import java.util.TimeZone

    val indian = Scalendar.now.tz("Indian/Chagos")
    val cst = Scalendar.now.tz("CST")

    val indiantz = TimeZone.getTimeZone("Indian/Chagos")
    val csttz = TimeZone.getTimeZone("CST")

    // Offsets from UTC
    indian.tz.offset should === (indiantz.getRawOffset )
    cst.tz.offset should === (csttz.getRawOffset)

    // Offsets from each other
    cst.tz.offset(indian) should === (12 * 1000 * 60 * 60)
  }

  "Periods" should "be created from adding fields together" in {
    val period = 4.days + 3.hours - 2.minutes

    val expected = (4 * 24 * 60 * 60 * 1000) +
                   (3 * 60 * 60 * 1000) -
                   (2 * 60 * 1000)

    period.milliseconds should === (expected)
    Hours(3).milliseconds should === (3 * 60 * 60 * 1000)
  }

  it should "be able to transform it's output" in {
    Hours(3).into.minutes should === (180)
    Days(4).into.hours should === (4 * 24)
  }

  it should "be able to add to times" in {
    val twoweeks = Weeks(1) + Days(7)
    val april = Scalendar(2011, 4, 1)
    val expected = april + Weeks(2)

    (april + twoweeks) should === (expected)
  }
}
