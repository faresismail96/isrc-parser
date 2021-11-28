package com.isrc.transform

import cats.data.Chain
import com.isrc.model.IsrcRecord
import cats.syntax.option._
import cats.syntax.traverse._
import cats.instances.list._
import cats.instances.option._

object Compute {

  def computeTotalAmount(list: List[IsrcRecord]): BigDecimal = {
    val totalAmount = list.map(_.amount).sum
    val roundedAmount =
      BigDecimal(totalAmount).setScale(2, BigDecimal.RoundingMode.HALF_UP)
    roundedAmount
  }

  def mergeRecords(list: List[IsrcRecord]): List[IsrcRecord] = {
    val res: List[IsrcRecord] = list
      .groupBy(_.title)
      .values
      .toList
      .traverse(combineIsrcRecords)
      .getOrElse(Nil)

    res
  }

  private[transform] def combineIsrcRecords(
      list: List[IsrcRecord]): Option[IsrcRecord] =
    list match {
      case Nil => None
      case ::(head, next) if next.forall(_.title == head.title) =>
        IsrcRecord(head.title, None, list.map(_.amount).sum).some
      case _ => None
    }
}
