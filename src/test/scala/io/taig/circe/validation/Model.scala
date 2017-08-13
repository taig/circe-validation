package io.taig.circe.validation

import cats.data.Validated._
import cats.data.ValidatedNel
import cats.syntax.cartesian._
import io.circe.generic.JsonCodec
import io.circe.{Decoder, Encoder}

object Validation {
  def email(value: String): ValidatedNel[String, String] =
    if (value.contains("@")) valid(value)
    else invalidNel("not an email")

  def min(length: Int)(value: String): ValidatedNel[String, String] =
    if (value.length < length) invalidNel(s"min $length")
    else valid(value)
}

case class Name(value: String) extends AnyVal

object Name {
  def lift(value: String): ValidatedNel[String, Name] =
    Validation.min(3)(value) map apply

  implicit val decoder: Decoder[Name] =
    Decoder[String].verify(lift)

  implicit val encoder: Encoder[Name] =
    Encoder[String].contramap(_.value)
}

case class Email(value: String) extends AnyVal

object Email {
  def lift(value: String): ValidatedNel[String, Email] =
    Validation.email(value) *> Validation.min(5)(value) map apply

  implicit val decoder: Decoder[Email] =
    Decoder[String].verify(lift)

  implicit val encoder: Encoder[Email] =
    Encoder[String].contramap(_.value)
}

@JsonCodec
case class Person(name: Name, email: Email)
