package com.isrc.validator

import cats.data.NonEmptyList
import org.scalatest.{FlatSpec, MustMatchers}
import cats.syntax.validated._
import cats.syntax.option._
import com.isrc.model.{Isrc, IsrcRecord, RecordValidationError}
import com.isrc.model.ValidationError._
import com.isrc.validator.RecordValidator._

class RecordValidatorSpec extends FlatSpec with MustMatchers {

  behavior of "Country code validator"

  it must "validate correct contry code" in {
    validateCountryCode("FR") mustBe ("FR".validNel)
    validateCountryCode("US") mustBe ("US".validNel)
  }
  it must "invalidate incorrect country code" in {
    validateCountryCode("XX") mustBe (InvalidCountryCode("XX").invalidNel)
    validateCountryCode("ZZ") mustBe (InvalidCountryCode("ZZ").invalidNel)
  }

  behavior of "Numeric amount validator"

  it must "validate correct numbers" in {
    numericAmount("123") mustBe 123.validNel
    numericAmount("1.0356") mustBe 1.0356.validNel
  }
  it must "invalidate non numeric values" in {
    numericAmount("hello") mustBe NonNumericAmount("hello").invalidNel
    numericAmount("123a") mustBe NonNumericAmount("123a").invalidNel
  }

  behavior of "mandatory value validator"

  it must "validate an existing value" in {
    mandatoryValue("test", TitleError) mustBe "test".validNel
    mandatoryValue("123", EmptyAmount) mustBe "123".validNel
  }

  it must "invalidate an empty values" in {
    mandatoryValue("", TitleError) mustBe TitleError.invalidNel
    mandatoryValue("   ", EmptyAmount) mustBe EmptyAmount.invalidNel
  }

  behavior of "validateISRC"

  it must "be valid for correct isrc" in {
    validateISRC("FRP1N0000160") mustBe Isrc("FR", "P1N", "00", "00160").some.validNel
    validateISRC("") mustBe None.validNel
    validateISRC(" ") mustBe None.validNel
  }
  it must "be invalid for incorrect isrc" in {
    validateISRC("ZZP1N0000160") mustBe InvalidCountryCode("ZZ").invalidNel
    validateISRC("Mytest") mustBe InvalidISRCFormat("Mytest").invalidNel
  }

  behavior of "validateAmount"

  it must "be valid for correct amounts" in {
    validateAmount("123.52") mustBe 123.52.validNel
  }
  it must "be invalid for incorrect amounts" in {
    validateAmount(" ") mustBe EmptyAmount.invalidNel
    validateAmount("hi") mustBe NonNumericAmount("hi").invalidNel
  }

  behavior of "validateRecord"

  it must "be valid for correct records" in {
    validateRecord(List("Malawëlëkaahm", "FRP1N0000160", "388.8834050870421")) mustBe IsrcRecord(
      "Malawëlëkaahm",
      Isrc("FR", "P1N", "00", "00160").some,
      388.8834050870421).validNel

    validateRecord(List("my title", "", "3.21")) mustBe IsrcRecord(
      "my title",
      None,
      3.21).validNel
  }
  it must "be invalid for incorrect records" in {
    validateRecord(List(" ", "", "3.21")) mustBe RecordValidationError(
      List(TitleError),
      "title:  , isrc: , amount: 3.21").invalidNel
    validateRecord(List(" ", "ZZP1N0000160", "test")) mustBe
      RecordValidationError(
        List(TitleError, InvalidCountryCode("ZZ"), NonNumericAmount("test")),
        "title:  , isrc: ZZP1N0000160, amount: test").invalidNel
  }
}
