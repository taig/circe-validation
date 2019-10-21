package io.taig.circe

import cats.data.{Validated, ValidatedNel}
import io.circe.{Decoder, DecodingFailure, HCursor, ValidatingDecoder}

package object validation {
  implicit final class RichValidationDecoder[A](val decoder: Decoder[A])
      extends AnyVal {
    def verify[B](lift: A => ValidatedNel[String, B]): ValidatingDecoder[B] =
      ValidatingDecoder.instance { cursor =>
        Validated
          .fromEither(decoder(cursor))
          .toValidatedNel
          .andThen(liftDecodingFailures(lift, cursor))
      }

    private def liftDecodingFailures[B](
        lift: A => ValidatedNel[String, B],
        cursor: HCursor
    ): A => ValidatedNel[DecodingFailure, B] =
      lift.andThen(_.leftMap(_.map(DecodingFailure(_, cursor.history))))
  }
}
