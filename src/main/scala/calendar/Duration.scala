package com.philipcali.utils
package calendar

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

  def traverse[A](value: Long)(fun: Duration => A) = {
    val (max, min) = if(delta.milliseconds < 0) {
      (0, (delta.milliseconds / value).toInt) 
    } else {
      ((delta.milliseconds / value).toInt, 0)
    }
    val mult = (num: Long) => if(num < 1) -1 * num else num

    for(iter <- (min to max);
        val dur = new Duration(start.time + mult(value * iter), 
                               end.time - mult(value * (max - iter))))
    yield(fun(dur))
  }

  def contains(cal: Scalendar) = cal isIn this
  def contains(time: Long) = Scalendar(time) isIn this 

  def reverse = new Duration(end.time, start.time)

  def by(value: Long): List[Duration] = {
    traverse(value)(dur => dur).toList
  }

  def next(value: Long) = {
    val incrementer = if(delta.milliseconds < 0) -1 else 1
    if(value > delta.milliseconds * incrementer) {
      new Duration(start.time + delta.milliseconds, end.time)
    } else {
      new Duration(start.time + value * incrementer, end.time)
    }
  }

  override def toString = "%s - %s" format(start, end)
}
