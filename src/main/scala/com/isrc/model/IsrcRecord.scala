package com.isrc.model

final case class IsrcRecord(title: String, isrc: Option[Isrc], amount: Double)

final case class Isrc(countryCode: String,
                      phonogramProducer: String,
                      year: String,
                      phonogramCode: String) {
  def show: String =
    s"ISRC $countryCode-$phonogramProducer-$year-$phonogramCode" // https://fr.wikipedia.org/wiki/International_Standard_Recording_Code
}
