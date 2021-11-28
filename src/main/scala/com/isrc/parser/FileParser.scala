package com.isrc.parser

import java.io.File

import cats.syntax.either._
import com.github.tototoshi.csv.{CSVReader, TSVFormat}
import com.isrc.model.ApplicationError._
import com.isrc.model.ApplicationError

object FileParser {

  def withReader(path: String)(
      f: CSVReader => Unit): Either[ApplicationError, Unit] =
    Either
      .catchNonFatal {
        implicit object TSVFormat extends TSVFormat
        val reader = CSVReader.open(new File(path))
        f(reader)
        reader.close()
      }
      .leftMap(err => FileFormatError(err))

}
