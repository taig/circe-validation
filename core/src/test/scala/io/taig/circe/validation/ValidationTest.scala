package io.taig.circe.validation

import cats.syntax.either._
import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import io.circe.{Decoder, DecodingFailure}
import io.circe.syntax._
import io.circe.generic.auto._
import org.scalatest.{FlatSpec, Matchers}

import io.taig.circe.validation.Name._
import io.taig.circe.validation.Email._

final class ValidationTest extends FlatSpec with Matchers {
  private val failuresToMessages
      : NonEmptyList[DecodingFailure] => NonEmptyList[String] = _.map(_.message)

  it should "not accumulate errors by default" in {
    val json = Email("asdf").asJson
    json.as[Email].leftMap(_.message) shouldBe Left("not an email")
  }

  it should "accumulate errors when using AccumulatingDecoders" in {
    val json = Email("asdf").asJson
    Email.decoder
      .decodeAccumulating(json.hcursor)
      .leftMap(failuresToMessages) shouldBe Invalid(
      NonEmptyList.of("not an email", "min 5")
    )
  }

  it should "not accumulate errors in nested Decoders by default" in {
    val json = Person(Name(""), Email("asdf")).asJson
    json.as[Person].leftMap(_.message) shouldBe Left("min 3")
  }

  it should "accumulate errors in nested Decoders" in {
    val json = Person(Name(""), Email("asdf")).asJson
    Decoder[Person]
      .decodeAccumulating(json.hcursor)
      .leftMap(failuresToMessages) shouldBe Invalid(
      NonEmptyList.of("min 3", "not an email", "min 5")
    )
  }
}
