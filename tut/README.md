# circe Validation

> Use cats Validated to create (Accumulating) circe Decoders

```tut:invisible
import io.taig.circe.validation.Build._
```

## Installation


```tut:evaluated
println {
    s"""
     |libraryDependencies += "$organization" %% "$normalizedName" % "$version"
     """.stripMargin.trim
}
```

## About

This repository is primarily an experimental exploration of validation handling with circe. My main concerns about the current implementation are:

 * `ValidatingDecoder` lives in `io.circe` to override the package scoped method `decodeAccumulating`
 * Validation failures cannot be distinguished from circe's `DecodingFailures`

## Usage

```tut:silent
import cats.implicits._
import cats.data.Validated._
import cats.data.ValidatedNel
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.Decoder
import io.taig.circe.validation._

object Validation {
  def email(value: String): ValidatedNel[String, String] =
    if (value.contains("@")) valid(value)
    else invalidNel("not an email")

  def min(length: Int)(value: String): ValidatedNel[String, String] =
    if (value.length < length) invalidNel(s"min $length")
    else valid(value)
}

case class Name(value: String) extends AnyVal
case class Email(value: String) extends AnyVal
case class Person(name: Name, email: Email)

def liftName(value: String): ValidatedNel[String, Name] =
  Validation.min(3)(value) map Name

def liftEmail(value: String): ValidatedNel[String, Email] =
  Validation.email(value) |+| Validation.min(5)(value) map Email

implicit val decoderName: Decoder[Name] = Decoder[String].verify(liftName)
implicit val decoderEmail: Decoder[Email] = Decoder[String].verify(liftEmail)
implicit val decoderPerson: Decoder[Person] = deriveDecoder

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