package com.isrc
import cats.data.Validated._
import com.isrc.parser.FileParser
import com.isrc.validator.RecordValidator
import cats.instances.list._
import cats.syntax.traverse._
import com.isrc.transform.{Compute, GenerateOutput}

object Application {

  def main(args: Array[String]): Unit = {
    val filePath = "src/main/resources/magma.tsv"
    val invalidPath = "src/main/resources/magmaInvalid.tsv"
    val outputPath = "src/main/resources/magmaOutput.tsv"

    FileParser.withReader(filePath) { reader =>
      val header :: data = reader.all()
      (for {
        records <- data
          .traverse(rec => RecordValidator.validateRecord(rec))
          .toEither
        mergedRecs = Compute.mergeRecords(records)
        _ <- GenerateOutput.reconstructFile(header, mergedRecs, outputPath)
      } yield (mergedRecs)) match {
        case Left(e) =>
          e.map(err => println(err.asString ++ "\n"))
          System.exit(1)
        case Right(value) =>
          val totalAmount = Compute.computeTotalAmount(value)
          println(s"The total amount is: $totalAmount")
          System.exit(0)
      }
    }
  }
}
