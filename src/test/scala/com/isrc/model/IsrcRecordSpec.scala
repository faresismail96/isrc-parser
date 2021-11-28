package com.isrc.model
import org.scalatest.{FlatSpec, MustMatchers}

class IsrcRecordSpec extends FlatSpec with MustMatchers {

  behavior of "show"

  it must "correctly print an ISRC record" in {
    Isrc("FR", "AB5", "07", "12345").show mustBe "ISRC FR-AB5-07-12345"
  }
}
