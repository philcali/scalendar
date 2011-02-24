# Scalendar

Scalendar is a pure Scala date api that interoperates quite nicely with
`java.util.Calendar` and `java.util.Date`. Ideas evolved from [my blog].

## Goals

  * Immutability
  * Interoperability
  * Easy date traversal
  * Easy date arithmetic

## Construction

A single import is all that's needed.

    import com.github.calendar.Scalendar

    val now = Scalendar.now

    // Use Calendar constants
    import java.util.Calendar._
    now.day(3).hour(15).month(MARCH)

    val april = Scalendar(year = 2012,
                          month = APRIL,
                          day = 1)

    val wed = now.set(DAY_OF_WEEK, WEDNESDAY)

## Date Arithmetic

Adding to a time, could never be easier.

    val tomorrow = Scalendar.now + (1 day)

    val far = tomorrow + (3 months) - (4 weeks)

    val away = far + (5 years) + (14 months) - (5667 days)

## Obtaining fields

Scalendar api provides a very uniform way to access calendar fields.
Every field has a `name` that tries to create an English name for the
field, and a `value`, which is its integer value

    val now = Scalendar.now

    now.day.name
    now.day.value
    now.month.name
    now.month.value
    now.isWeekday
    now.isWeekend
    now.day.inWeek
    now.day.inYear
    now.week.value
    // etc

## Creating Durations

Creating a meaningful duration of time very simple to create with 
the `to` dsl word.

    val duration = Scalendar.now to (Scalendar.now + (1 week))

    // Determining delta 
    duration.delta.days
    duration.delta.months
    // etc

## Traversing a Duration

There are two ways to traverse a duration:

  1. The `traverse` method
  2. The `by` dsl word

    // by will return a list of durations, which
    // can be operated on as a List
    // This only returns MWF
    val mwf = duration.by(1 day) filter(_.day.value match {
      case MONDAY | WEDNESDAY | FRIDAY => true
      case _ => false
    })

    // traverse returns by traversal
    val html = 
    <table>{
      duration.traverse(1 week) { weekDur =>
        <tr>{
          weekDur.traverse(1 day) { dayDur =>
            <td>{ dayDur.day.value.toString }</td>
          }
        }</tr>
      }
    }</table>

    println(html)

View more examples of how to use the library in the test source file.

## Requirements

  * scala 2.8.0, 2.8.1


[my blog]: http://philcalicode.blogspot.com/
