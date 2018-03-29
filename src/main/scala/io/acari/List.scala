package io.acari

/**
  * Forged in the flames of battle by alex.
  */
sealed trait List[+T]

case object Nil extends List[Nothing]

case class Cons[+T](head: T, tail: List[T]) extends List[T]


object List extends App {

  def sum(ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }

  def product(doubles: List[Double]): Double = doubles match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }

  def apply[T](as: T*): List[T] =
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  def pop[T](list: List[T]): List[T] = list match {
    case Cons(x, xs) => xs
  }

  def peek[T](list: List[T]): T = list match {
    case Cons(x, xs) => x
  }

  def drop[T](list: List[T], n: Int): List[T] =
    if (n < 2) pop(list)
    else drop(pop(list), n - 1)

  def dropWhile[T](list: List[T], shouldRemove: T => Boolean): List[T] =
    if (shouldRemove(peek(list))) dropWhile(pop(list), shouldRemove)
    else list

  def init[T](list: List[T]): List[T] = list match {
    case Cons(x, Cons(y, Nil)) => Cons(x, Nil)
    case Cons(x, Cons(y, z)) => Cons(x, init(Cons(y, z)))
  }


  override def main(args: Array[String]): Unit = {

    val result: Int = List(1, 2, 3, 4, 5) match {
      case Cons(x, Cons(2, Cons(4, _))) => x
      case Nil => 42
      case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
      case Cons(h, t) => h + sum(t)
      case _ => 101
    }
    println(result)

    val removeOne: List[Int] = List(5, 4, 3, 2, 1) match {
      case Cons(x, xs) => xs
    }
    println(removeOne)

    val swapOne = List("me me", "big", "boy") match {
      case Cons(x, xs) => Cons("Me Me", xs)
    }

    println(swapOne)


    val dropTheBase = drop(List("ohh", "dats", "vewy", "nice"), 2)

    println(dropTheBase)

    val vewyNice = dropWhile[String](List("ohh", "dats", "vewy", "nice"),
      s => !s.equals("nice"))

    println(vewyNice)

    val allButOne = init(List(5,4,3,2,1))
    println(allButOne)


  }
}