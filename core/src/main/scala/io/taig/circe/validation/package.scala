package io.taig.circe

import cats.data.ValidatedNel
import io.circe.{Decoder, ValidatingDecoder}

package object validation {
  implicit final class RichValidationDecoder[A](val decoder: Decoder[A]) extends AnyVal {
    def verify[B](validate: A => ValidatedNel[String, B]): ValidatingDecoder[B] =
      ValidatingDecoder.lift(decoder)(validate)
  }
}
