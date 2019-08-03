package com.fragnostic.dao.support

import java.sql.ResultSet

/**
 * Created by fernandobrule on 9/17/16.
 */
trait RecursionSupport {

  def newList[T](
    resultSet: ResultSet,
    newEntity: ResultSet => T): List[T] =
    if (resultSet.next()) {
      newEntity(resultSet) :: newList(resultSet, newEntity)
    } else {
      Nil
    }

}
