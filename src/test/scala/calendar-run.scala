package scalendar
package test

object Main {
  def main(args: Array[String]) {
    def line(what: String)(block: => Unit) = {
      println(what)
      println("-" * 30)
      block
      println("-" * 30)
    }

    // Patterns ... Explicit declaring implicit string conversions
    val pattern = Pattern("MMMM dd, yyyy KK:mm:ss.S a")
    implicit val con = Pattern("MM/dd/yyyy")

    // Getting now
    val current = Scalendar.now

    // Time arithmetic
    line("Date Arithmetic") {
      val tomorrow = current + 1.day

      line("now + 1.day") {
        println("now: " + current)
        println(tomorrow)
      }

      line("tomorrow + 7.days") {
        println(tomorrow + 7.days)
      }
     
      line("now - 1.day") {
        println(current - 1.day)
      }

      line("day in week") {
        println("Coming Saturday: " + current.inWeek(Day.Saturday))
        println("Last Sunday: " + current.inWeek(Day.Sunday))
        println("Next Sunday: " + (current + 1.week).inWeek(Day.Sunday))
      }
    }
    // Periods of Time
    val period = 1.day + 4.hours + 3.weeks
    line("Periods of Time") {
      line("now + period") {
        println(current + period)
      }

      line("period conversion") {
        println("months: " + period.into.months)
        println("days: " + period.into.days)
        println("hours: " + period.into.hours)
        println("minutes: " + period.into.minutes)
      }
    }

    // Durations
    line("Durations") {
      // Display Calendar Convenience
      line("Calendar Day") {
        println(current.calendarDay)
      }

      line("Calendar Week") {
        current.calendarWeek by 1.day foreach { d =>
          println(d.day.name + ": " + d)
        }
      }

      line("Calendar Month") {
        current.calendarMonth by 1.week foreach { wd =>
          println(wd.week.name + " week with days: ")
          println(wd by 1.day map(_.day.value) mkString(", "))
        }
      }

      line("Duration creation with 'to'") {
        println(current to current + 4.days)
        println(current to current + 3.weeks + 3.days)
      }

      line("Duration deltas") {
        println("Days in: " + current.calendarMonth.delta.days)
        println("Hours in: " + current.calendarDay.delta.hours)
      }

      line("Duration are traversable") {
        println(<th>{current.calendarWeek.traverse(1.day) { dd =>
          <td>{dd.day.name}</td>
        }}</th>)
      }

      line("Use for comprehensions if you want") {
        current.calendarMonth by 1.week foreach { wd =>
          for(dd <- wd by 1.day; if dd.isWeekend) (print(dd.day.value + " "))
          println
        }
      }
      
      line("Mixing Scala traversables") {
        val weekdays = current.calendarMonth by 1.day filter (_.isWeekday)
        val m = weekdays map { d =>
          "Working on %s the %d" format(d.day.name, d.day.value)
        }
        println(m mkString(", "))
      }
    }

    line("Timezone Manipulation") {
      val pst = current.tz("PST")
      val est = current.tz("EST5EDT")

      line("Setting a new timezone for an existing date") {
        println(pst)
        println(est)
      }

      line("Determining the offset from GMT or other time") {
        // In this case, PST is -28800000 milliseconds "to the left" of GMT
        println(pst.tz.offset)
        // In this case, EST is 3 hours "to the right" of PST
        println(pst.tz.offset(est).milliseconds.into.hours)
      }
    }
  }
}
