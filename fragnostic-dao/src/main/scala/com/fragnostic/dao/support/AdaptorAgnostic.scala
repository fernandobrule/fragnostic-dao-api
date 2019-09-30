package com.fragnostic.dao.support

import java.sql.{ Timestamp, Date => JSqlDate }
import java.text.SimpleDateFormat
import java.util.{ Date => JUtilDate }

/**
 * Created by Fernando Brule on 30-06-2015 22:23:00.
 * Generated by Tesseract.
 */
trait AdaptorAgnostic {

  def __deletemeplz__stringDateToSqlDate(stDate: String, pattern: String): JSqlDate = {
    val dateFormat = new SimpleDateFormat(pattern)
    new JSqlDate(dateFormat.parse(stDate).getTime)
  }

  def __deletemeplz__stringDateToSqlTimestamp(stDate: String, pattern: String): Timestamp = {
    val dateFormat = new SimpleDateFormat(pattern)
    new Timestamp(dateFormat.parse(stDate).getTime)
  }

  def __deletemeplz__sqlDateToString(date: JSqlDate, pattern: String): String = {
    if (date != null) {
      val dateFormat = new SimpleDateFormat(pattern)
      dateFormat.format(new JUtilDate(date.getTime))
    } else {
      ""
    }
  }

  def __deletemeplz__sqlTimestampToString(timestamp: Timestamp, pattern: String): String = {
    if (timestamp != null) {
      val dateFormat = new SimpleDateFormat(pattern)
      dateFormat.format(new JUtilDate(timestamp.getTime))
    } else {
      ""
    }
  }

}
