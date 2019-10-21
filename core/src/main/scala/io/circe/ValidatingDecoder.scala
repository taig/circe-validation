package io.circe

import cats.syntax.either._

trait ValidatingDecoder[A] extends Decoder[A] {
  override def apply(cursor: HCursor): Decoder.Result[A] =
    decodeValidating(cursor).toEither.leftMap(_.head)

  override def decodeAccumulating(
      cursor: HCursor
  ): Decoder.AccumulatingResult[A] =
    decodeValidating(cursor)

  def decodeValidating(cursor: HCursor): Decoder.AccumulatingResult[A]
}

object ValidatingDecoder {
  def instance[A](
      f: HCursor => Decoder.AccumulatingResult[A]
  ): ValidatingDecoder[A] =
    new ValidatingDecoder[A] {
      override def decodeValidating(
          cursor: HCursor
      ): Decoder.AccumulatingResult[A] = f(cursor)
    }
}
