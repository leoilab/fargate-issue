package com.leoilab.fargateissue

import cats.Applicative
import cats.implicits._

import scala.util.Random

trait LongStrings[F[_]]{
  def longString(n: LongStrings.Length): F[String]
}

object LongStrings {
  implicit def apply[F[_]](implicit ev: LongStrings[F]): LongStrings[F] = ev
  final case class Length(length: Int) extends AnyVal

  def impl[F[_]: Applicative]: LongStrings[F] = (n: LongStrings.Length) => Random.alphanumeric.take(n.length).mkString.pure[F]
}