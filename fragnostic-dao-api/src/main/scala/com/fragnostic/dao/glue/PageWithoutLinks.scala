package com.fragnostic.dao.glue

case class PageWithoutLinks[P](
  numPage: Integer,
  orderBy: String,
  rowsPerPage: Int,
  numRows: Int,
  numPages: Int,
  list: List[P],
  listIsEmpty: Boolean //
)

