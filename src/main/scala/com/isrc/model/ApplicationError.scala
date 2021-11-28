package com.isrc.model

sealed trait ApplicationError {
  def asString: String
}

object ApplicationError {
  final case class FileFormatError(err: Throwable) extends ApplicationError {
    val asString =
      "Encountered an error with the file format: ${err.getMessage}. Stack trace: ${err.getStackTrace}"
  }
  final case class UnknownProcessingError(err: Throwable)
      extends ApplicationError {
    override def asString: String =
      s"Encountered an unknown processing error: ${err.getMessage}. Stack trace: ${err.getStackTrace}"
  }
}
sealed trait ValidationError extends ApplicationError

final case class RecordValidationError(err: List[ValidationError],
                                       originalRecord: String)
    extends ApplicationError {
  def asString: String =
    s"""Encountered errors during the validation of the record: $originalRecord
       |errors: ${err.map(_.asString).mkString(", ")}
       |""".stripMargin

}

object ValidationError {
  case object TitleError extends ValidationError {
    val asString = "Title cannot be empty"
  }
  case object EmptyAmount extends ValidationError {
    val asString = "Amount cannot be empty"
  }
  final case class NonNumericAmount(amount: String) extends ValidationError {
    def asString = s"Amount must be numeric. Received: $amount"
  }
  final case class UnknownValidationError(throwable: Throwable)
      extends ValidationError {
    def asString =
      s"Encountered an unknown validation error: ${throwable.getMessage}. Stack trace: ${throwable.getStackTrace}"
  }
  case object UnknownFormatValidationError extends ValidationError {
    override def asString: String =
      "Unknown Error during the format validation."
  }

  final case class InvalidISRCFormat(record: String) extends ValidationError {
    val asString = s"The ISRC format is invalid. Received: $record"
  }
  final case class InvalidCountryCode(code: String) extends ValidationError {
    val asString = s"The ISRC country code is invalid. Received: $code"
  }

}
