package com.isrc.transform
import com.isrc.model.{Isrc, IsrcRecord}
import org.scalatest.{FlatSpec, MustMatchers}
import cats.syntax.option._
class ComputeSpec extends FlatSpec with MustMatchers {

  behavior of "computeTotalAmount"

  it must "correctly compute and round total amount" in {
    val recs1 =
      List(IsrcRecord("a", None, 0.122123), IsrcRecord("b", None, 1.2222))
    Compute.computeTotalAmount(recs1) mustBe 1.34

    val recs2 =
      List(IsrcRecord("a", None, 0.455555), IsrcRecord("b", None, 1.333333))
    Compute.computeTotalAmount(recs2) mustBe 1.79

    val recs3 =
      List(IsrcRecord("a", None, 0.300000), IsrcRecord("b", None, 1.3050)) // Round half up
    Compute.computeTotalAmount(recs3) mustBe 1.61
  }

  behavior of "combineIsrcRecords"

  it must "correctly combine records with same title" in {
    val recs1 = List(IsrcRecord("a", None, 1), IsrcRecord("a", None, 2))
    Compute.combineIsrcRecords(recs1) mustBe Some(IsrcRecord("a", None, 3))

    val recs2 = List(IsrcRecord("b", Isrc("FR", "AB5", "07", "12345").some, 1),
                     IsrcRecord("b", None, 2))
    Compute.combineIsrcRecords(recs2) mustBe Some(IsrcRecord("b", None, 3))
  }

  it must "not combine records with different titles" in {
    val recs1 = List(IsrcRecord("a", None, 1), IsrcRecord("b", None, 2))
    Compute.combineIsrcRecords(recs1) mustBe None
  }

  behavior of "mergeRecords"

  it must "correctly merge records with same title" in {
    val recs1 = List(IsrcRecord("a", None, 1),
                     IsrcRecord("a", None, 10.555),
                     IsrcRecord("b", None, 2))

    Compute.mergeRecords(recs1) mustBe List(IsrcRecord("a", None, 11.555),
                                            IsrcRecord("b", None, 2))
  }
}
