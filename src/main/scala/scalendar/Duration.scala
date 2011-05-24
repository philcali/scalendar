package com.github.philcali.scalendar

import conversions._
import operations.RichSupport

class Duration(from: Long, last: Long) extends RichSupport {
  val start = new Scalendar(from)
  val end = new Scalendar(last)

  // This gives a duration rather strong support for
  // pulling calendar values
  protected val javaTime = toCalendar(start)

  def delta = new ToConversion(end.time - start.time)

  def to(spot: Scalendar) = 
    new Duration(start.time, spot.time)

  def to(duration: Duration) =
    new Duration(start.time, duration.end.time)

  def + (duration: Duration) =
    new Duration(start.time, end.time + duration.delta.milliseconds)

  def - (duration: Duration) =
    new Duration(start.time, end.time - duration.delta.milliseconds)

  def traverse[A](value: Evaluated)(fun: Duration => A): List[A] = {
    val mult = if(delta.milliseconds < 0) -1 else 1
    val newVal = Evaluated(value.field, value.number * mult)

    def continueCond(cal: Scalendar) = if(mult == -1) cal >= end else cal <= end

    def traverseTimes(times: Int): List[A] = {
      def repeat(cal: Scalendar) = (0 until times).foldLeft(cal) {(a, b) => 
        a + newVal
      }

      val newStart = repeat(start) 
      val newEnd = newStart + newVal - (1 second)

      continueCond(newEnd) match {
        case true => fun(newStart to newEnd) :: traverseTimes(times + 1)
        case false => Nil
      }
    }
    
    traverseTimes(0)
  }

  def contains(cal: Scalendar) = cal isIn this
  def contains(time: Long) = Scalendar(time) isIn this 

  def reverse = new Duration(end.time, start.time)

  def by(value: Evaluated): List[Duration] = {
    traverse(value)(dur => dur)
  }

  override def toString = "%s - %s" format(start, end)
}
