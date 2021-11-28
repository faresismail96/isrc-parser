package com.isrc.transform
import java.io.File

import cats.data.{EitherNel, NonEmptyList}
import cats.syntax.either._
import com.github.tototoshi.csv.{CSVWriter, TSVFormat}
import com.isrc.model.ApplicationError.UnknownProcessingError
import com.isrc.model.{ApplicationError, IsrcRecord}

object GenerateOutput {
  def reconstructFile(header: List[String],
                      data: List[IsrcRecord],
                      outputPath: String): EitherNel[ApplicationError, Unit] = {
    Either
      .catchNonFatal {
        implicit object TSVFormat extends TSVFormat
        val writer = CSVWriter.open(new File(outputPath))

        val res = data
          .sortBy(_.amount)(Ordering[Double].reverse)
          .map(rec => List(rec.title, rec.amount))
        writer.writeRow(header)
        writer.writeAll(res)
        writer.close()
      }
      .leftMap(x => NonEmptyList.of(UnknownProcessingError(x)))
  }
}
