package com.leoilab.fargateissue

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object FargateissueRoutes {
  def helloWorldRoutes[F[_]: Sync](H: LongStrings[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "longString" / length =>
        for {
          longString <- H.longString(LongStrings.Length(length.toInt))
          resp <- Ok(longString)
        } yield resp
      case GET -> Root => Ok("yep")
    }
  }
}