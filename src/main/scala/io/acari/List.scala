package io.acari

/**
  * Forged in the flames of battle by alex.
  */
sealed trait List[+A]

case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]


object List {

  def sum(ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }

  def product(doubles: List[Double]): Double = doubles match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }

//todo: make this work
//  override def main(args: Array[String]): Unit = {
//
//    val result : Int = List(1,2,3,4,5) match {
//      case Cons(x, Cons(2, Cons(4, _))) => x
//      case Nil => 42
//      case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
//      case Cons(h, t) => h + t
//      case _ => 101
//    }
//
//    println(result)result
//  }
}