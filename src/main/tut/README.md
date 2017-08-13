# circe Validation

> Use cats Validated to create (Accumulating) circe Decoders Edit
  Add topics

## Installation

The project hasn't been published yet.

## About

This repository is primarily an experimental exploration of validation handling with circe. My main concerns about the current implementation are:

 * `ValidatingDecoder` lives in `io.circe` to override the package scoped method `decodeAccumulating`
 * Validation failures cannot be distinguished from circe's `DecodingFailures` 

## Usage

```tut:silent
import cats.syntax.show._
import cats.data.Validated._
import cats.data.ValidatedNel
import cats.syntax.cartesian._
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.{Decoder, Encoder}
import io.taig.circe.validation._

object Validation {
  def email(value: String): ValidatedNel[String, String] =
    if (value.contains("@")) valid(value)
    else invalidNel("not an email")

  def min(length: Int)(value: String): ValidatedNel[String, String] =
    if (value.length < length) invalidNel(s"min $length")
    else valid(value)
}

case class Name(value: String) extends AnyVal; object Name {
  def lift(value: String): ValidatedNel[String, Name] =
    Validation.min(3)(value) map (new Name(_))

  implicit val decoder: Decoder[Name] =
    Decoder[String].verify(lift)

  implicit val encoder: Encoder[Name] =
    Encoder[String].contramap(_.value)
}

case class Email(value: String) extends AnyVal; object Email {
  def lift(value: String): ValidatedNel[String, Email] =
    Validation.email(value) *> Validation.min(5)(value) map (new Email(_))

  implicit val decoder: Decoder[Email] =
    Decoder[String].verify(lift)

  implicit val encoder: Encoder[Email] =
    Encoder[String].contramap(_.value)
}

@JsonCodec
case class Person(name: Name, email: Email)

val text = """{ "name":"Qt", "email":"foo" }"""
val Right(json) = parse(text)
val cursor = json.hcursor

// Default behavior is decoding without accumulation
val Left(decodingFailure) = Decoder[Person].apply(cursor)
val Invalid(accumulatedDecodingFailures) = Decoder[Person].accumulating.apply(cursor)
```

```tut:book
decodingFailure.show
accumulatedDecodingFailures.show
```