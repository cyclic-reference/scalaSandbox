package io.acari

/**
  * Forged in the flames of battle by alex.
  */
sealed trait Either[+E, +T] {
  def map[U](f: T => U): Either[E, U] = this match {
    case Left(e) => Left(e)
    case Right(t) => Right(f(t))
  }

  def flatMap[EE >: E, U](f: T => Either[EE, U]): Either[EE, U] = this match {
    case Left(e) => Left(e)
    case Right(t) => f(t)
  }
  def orElse[EE >: E, U >: T](u: => Either[EE, U]): Either[EE, U] = this match {
    case Left(_) => u
    case Right(_) => this
  }

  def map2[EE >: E, U, V](uE: Either[EE, U])(f: (T, U) => V): Either[EE, V] =
    flatMap(t => uE.map(u=> f(t, u)))
}

case class Left[+E](value: E) extends Either[E, Nothing]

case class Right[+T](value: T) extends Either[Nothing, T]