package io.circe

import cats.data.{Validated, ValidatedNel}
import cats.syntax.either._

trait ValidatingDecoder[A] extends Decoder[A] {
  override def apply(cursor: HCursor): Decoder.Result[A] = decodeValidating(cursor).toEither.leftMap(_.head)

  override def decodeAccumulating(cursor: HCursor): Decoder.AccumulatingResult[A] = decodeValidating(cursor)

  def decodeValidating(cursor: HCursor): Decoder.AccumulatingResult[A]
}

object ValidatingDecoder {
  def instance[A](f: HCursor => Decoder.AccumulatingResult[A]): ValidatingDecoder[A] =
    new ValidatingDecoder[A] {
      override def decodeValidating(cursor: HCursor): Decoder.AccumulatingResult[A] = f(cursor)
    }

  def lift[A, B](decoder: Decoder[A])(validate: A => ValidatedNel[String, B]): ValidatingDecoder[B] =
    ValidatingDecoder.instance { cursor =>
      Validated
        .fromEither(decoder(cursor))
        .toValidatedNel
        .andThen(liftDecodingFailures[A, B](validate, cursor))
    }

  private def liftDecodingFailures[A, B](
      validate: A => ValidatedNel[String, B],
      cursor: HCursor
  ): A => ValidatedNel[DecodingFailure, B] = validate.andThen(_.leftMap(_.map(DecodingFailure(_, cursor.history))))

  def apply[A](implicit decoder: Decoder[A]): ApplyBuilder[A] = new ApplyBuilder[A](decoder)

  final class ApplyBuilder[A](val decoder: Decoder[A]) extends AnyVal {
    def apply[B](validate: A => ValidatedNel[String, B]): ValidatingDecoder[B] = lift(decoder)(validate)
  }
}
