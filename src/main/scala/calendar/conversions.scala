package com.github.philcali.calendar
package conversions

import java.util.Calendar._

case class Evaluated(field: Int, number: Int)

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


