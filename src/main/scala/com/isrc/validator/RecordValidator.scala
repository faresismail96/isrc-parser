package com.isrc.validator

import java.util.Locale

import cats.data.{NonEmptyList, ValidatedNel}
import cats.data.Validated._
import cats.syntax.validated._
import cats.syntax.apply._
import cats.syntax.option._
import com.isrc.model._
import com.isrc.model.ValidationError._

import scala.util.{Failure, Success, Try}

object RecordValidator {

  def validateRecord(
      record: List[String]): ValidatedNel[ApplicationError, IsrcRecord] =
    record match {
      case List(title, isrc, amount) =>
        val originalRecord = s"title: $title, isrc: $isrc, amount: $amount"
        val validatedRecord = (mandatoryValue(title, TitleError),
                               validateISRC(isrc),
                               validateAmount(amount))
          .mapN((title, isrc, amount) => IsrcRecord(title, isrc, amount))
        validatedRecord.leftMap(ae =>
          NonEmptyList.of(RecordValidationError(ae.toList, originalRecord)))
      case _ => UnknownFormatValidationError.invalidNel
    }

  def validateAmount(amount: String): ValidatedNel[ValidationError, Double] =
    mandatoryValue(amount, EmptyAmount).andThen(numericAmount)

  def validateISRC(
      isrc: String): ValidatedNel[ValidationError, Option[Isrc]] = {
    val isrcRegex = "([a-zA-Z]{2})(.{3})(\\d{2})(\\d{5})".r
    isrc match {
      case a if a.trim.isEmpty => None.validNel
      case isrcRegex(countryCode, phonogramProducer, year, phonogramCode) =>
        validateCountryCode(countryCode).map(code =>
          Isrc(code, phonogramProducer, year, phonogramCode).some)
      case _ => InvalidISRCFormat(isrc).invalidNel
    }
  }

  def mandatoryValue(
      value: String,
      error: ValidationError): ValidatedNel[ValidationError, String] =
    if (value.trim.nonEmpty) value.validNel else error.invalidNel

  def numericAmount(amount: String): ValidatedNel[ValidationError, Double] =
    Try(amount.toDouble.validNel) match {
      case Failure(_: NumberFormatException) =>
        NonNumericAmount(amount).invalidNel
      case Failure(b: Throwable) => UnknownValidationError(b).invalidNel
      case Success(value)        => value
    }

  def validateCountryCode(code: String): ValidatedNel[ValidationError, String] =
    if (Locale.getISOCountries.contains(code)) code.validNel
    else InvalidCountryCode(code).invalidNel
}
