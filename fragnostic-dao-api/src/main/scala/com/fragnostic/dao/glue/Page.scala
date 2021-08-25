package com.fragnostic.dao.glue

case class Page[P](
  numPage: Integer,
  orderBy: String,
  linksLimitLeft: Int,
  linksLimitRight: Int,
  links: List[Int],
  rowsPerPage: Int,
  numRows: Int,
  numPages: Int,
  list: List[P],
  listIsEmpty: Boolean //
)
