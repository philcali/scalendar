package com.github.philcali.scalendar.conversions

import java.util.Calendar._

import com.github.philcali.scalendar.{
  Scalendar,
  implicits
}
import implicits._

case class Period (fields: List[Evaluated]) {
  def + (other: Evaluated) = 
    Period(other :: fields)
  def - (other: Evaluated) = 
    Period(other.negate :: fields)
  def milliseconds = {
    val now = Scalendar.now
    val future = fields.foldLeft(now)(_ + _)
    (now to future).delta.milliseconds
  }
  def into = new ToConversion(this.milliseconds)
}

case class Evaluated(field: Int, number: Int) {
  def negate = Evaluated(field, -number)
}

class FromConversion(number: Int) {
  def millisecond = Evaluated(MILLISECOND, number)
  def second = Evaluated(SECOND, number)
  def minute = Evaluated(MINUTE, number)
  def hour = Evaluated(HOUR, number)
  def day = Evaluated(DATE, number)
  def week = Evaluated(WEEK_OF_MONTH, number)
  def month = Evaluated(MONTH, number)
  def year = Evaluated(YEAR, number)

  def milliseconds = millisecond
  def seconds = second
  def minutes = minute
  def hours = hour
  def days = day
  def weeks = week
  def months = month
  def years = year
}

class ToConversion(number: Long) {
  def milliseconds = number
  def seconds = number / 1000
  def minutes = seconds / 60
  def hours = minutes / 60
  def days = hours / 24
  def weeks = days / 7
  def months  = days / 30
  def years = days / 365
}


