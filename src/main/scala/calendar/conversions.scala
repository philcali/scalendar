package com.philipcali.utils.calendar
package conversions

class FromConversion(number: Long) {
  def second = number * 1000
  def minute = second * 60
  def hour = minute * 60
  def day = hour * 24
  def week = day * 7
  def month = day * 30
  def year = day * 365

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


