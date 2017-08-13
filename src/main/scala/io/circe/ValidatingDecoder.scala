package io.circe

import cats.syntax.either._

trait ValidatingDecoder[A] extends Decoder[A] {
  override def apply(cursor: HCursor): Decoder.Result[A] =
    decodeValidating(cursor).toEither.leftMap(_.head)

  override private[circe] def decodeAccumulating(
      cursor: HCursor): AccumulatingDecoder.Result[A] =
    decodeValidating(cursor)

  def decodeValidating(cursor: HCursor): AccumulatingDecoder.Result[A]
}

object ValidatingDecoder {
  def instance[A](
      f: HCursor => AccumulatingDecoder.Result[A]): ValidatingDecoder[A] =
    new ValidatingDecoder[A] {
      override def decodeValidating(
          cursor: HCursor): AccumulatingDecoder.Result[A] = f(cursor)
    }
}
