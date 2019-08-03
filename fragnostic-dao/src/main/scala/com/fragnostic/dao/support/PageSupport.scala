package com.fragnostic.dao.support

import scala.collection.mutable.{ ListBuffer => MList }

/**
 * Created by fernandobrule on 8/1/15.
 */
trait PageSupport {

  def getPageLinks(
    numPage: Int,
    numPages: Int,
    numMaxBadgets: Short): (Int, Int, List[Int]) = {

    val limitLeft = {
      val shiftLeft = numPage - numMaxBadgets
      if (shiftLeft < 1) 1
      else shiftLeft
    }

    val limitRight = {
      val shiftRight = numPage + numMaxBadgets
      if (shiftRight > numPages) numPages
      else shiftRight
    }

    val list = new MList[Int]()

    if (limitLeft > 1) {
      list += 1
    }

    limitLeft to limitRight foreach (
      i => list += i)

    if (limitRight < numPages) {
      list += numPages
    }

    (limitLeft, limitRight, list.toList)
  }

  def getNumPage(
    numPage: Int,
    numPages: Int): Int = {
    if (numPage < 1) 1 //Left("page.support.error.num.page.lt.one")
    else if (numPage > numPages) numPages
    //Left("page.support.error.num.page.solicitada.gt.num.pages.availables")
    else numPage
  }

  def getNumPages(
    numRows: Int,
    rowsPerPage: Int): Int = {
    val numPages: Int = numRows / rowsPerPage
    val remainder: Int = numRows % rowsPerPage // for the sake of clarity
    if (numPages == 0) 1
    else if (remainder > 0) numPages + 1
    else numPages
  }

}
