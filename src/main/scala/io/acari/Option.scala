package io.acari

sealed trait Option[+T] {
  def orElse[U >: T](ob: => Option[U]): Option[U] =
    this map (Some(_)) getOrElse ob

  def map[U](f: T => U): Option[U] = this match {
    case None => None
    case (s: Some[T]) => Some(f(s.get))
  }

  def getOrElse[U >: T](default: => U): U = this match {
    case None => default
    case Some(t) => t
  }

  def filter(f: T => Boolean): Option[T] =
    flatMap(t => if (f(t)) Some(t) else None)

  def flatMap[U](f: T => Option[U]): Option[U] = {
    map(f) getOrElse None
  }


}

case class Some[+T](get: T) extends Option[T]

case object None extends Option[Nothing]



