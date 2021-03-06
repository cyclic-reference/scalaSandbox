package io.acari

import io.acari.stream._

trait RNG {
  def nextInt: (Int, RNG) // Should generate a random `Int`. We'll later define other functions in terms of `nextInt`.
}

object RNG {


  // NB - this was called SimpleRNG in the book text

  def main(args: Array[String]): Unit = {
    val rng = Simple(9001L)
    val (i1, rng1) = nonNegativeInt(rng)
    println(i1)
    val (i2, ng2) = nonNegativeInt(rng1)
    println(i2)
    val (j1, rng_1) = double(rng)
    println(j1)
    val (j2, rng_2) = double(rng1)
    println(j2)
    val (iList, rng69) = ints(5)(rng)
    println(iList)
  }

  case class Simple(seed: Long) extends RNG {
    def nextInt: (Int, RNG) = {
      val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL // `&` is bitwise AND. We use the current seed to generate a new seed.
      val nextRNG = Simple(newSeed) // The next state, which is an `RNG` instance created from the new seed.
      val n = (newSeed >>> 16).toInt // `>>>` is right binary shift with zero fill. The value `n` is our new pseudo-random integer.
      (n, nextRNG) // The return value is a tuple containing both a pseudo-random integer and the next `RNG` state.
    }
  }

  type Rand[+A] = RNG => (A, RNG)

  val int: Rand[Int] = _.nextInt

  def unit[A](a: A): Rand[A] =
    butt => (a, butt)

  def map[A, B](s: Rand[A])(f: A => B): Rand[B] =
    flatMap(s)(a => unit(f(a)))

  def nonNegativeInt(rng: RNG): (Int, RNG) = {
    val (i1, rng1) = rng.nextInt
    (if (i1 < 0) -(i1 + 1) else i1, rng1)
  }

  def nonNegativeInt2(rng: RNG): Rand[Int] = {
    val (i1, rng1) = rng.nextInt
    unit(if (i1 < 0) -(i1 + 1) else i1)
  }

  def double(rng: RNG): (Double, RNG) = {
    val (ranbo, rng1) = nonNegativeInt(rng)
    val ofTen = Math.ceil(Math.log10(ranbo.toDouble)) * 10D
    (ranbo.toDouble / ofTen, rng1)
  }

  def double2(rNG: RNG): Rand[Double] = {
    map(nonNegativeInt2(rNG))(ranbo => ranbo / (Math.ceil(Math.log10(ranbo.toDouble)) * 10))
  }

  def intDouble(rng: RNG): ((Int, Double), RNG) = {
    val (i, rng1) = rng.nextInt
    val (d, rng2) = double(rng1)
    ((i, d), rng2)
  }

  def doubleInt(rng: RNG): ((Double, Int), RNG) = {
    val ((i, d), rng1) = intDouble(rng)
    ((d, i), rng1)
  }

  def double3(rng: RNG): ((Double, Double, Double), RNG) = {
    val (d, rng1) = double(rng)
    val (d2, rng2) = double(rng1)
    val (d3, rng3) = double(rng2)
    ((d, d2, d3), rng3)
  }

  def ints(count: Int)(rng: RNG): (List[Int], RNG) = {
    val tFirst = nonNegativeInt(rng)
    Stream.unfold(tFirst) { case (f0, f1) => scala.Some((f0, nonNegativeInt(f1))) }
      .take(count - 1)
      .foldRight((List(tFirst._1), tFirst._2))((i, t) =>
        (io.acari.Cons(i._1, t._1), t._2))
  }

  def map2[A, B, C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = {
    flatMap(ra)(a => map(rb)(b => f(a, b)))
  }


  def sequence[A](fs: List[Rand[A]]): Rand[List[A]] =
    List.reduceLeft(fs, unit(List[A]()))((u, t) => map2(u, t)((a, b) => io.acari.Cons(b, a)))

  def flatMap[A, B](f: Rand[A])(g: A => Rand[B]): Rand[B] = {
    rng => {
      val (a, rng2) = f(rng)
      g(a)(rng2)
    }
  }

  def nonNegativeLessThan(n: Int): Rand[Int] =
    flatMap(nonNegativeInt) { a =>
      val mod = a % n
      if (a + (n - 1) - mod >= 0)
        unit(mod)
      else
        nonNegativeLessThan(n)
    }

}

case class State[S, +A](run: S => (A, S)) {
  def map[B](f: A => B): State[S, B] =
    flatMap(a => State.unit(f(a)))

  def map2[B, C](sb: State[S, B])(f: (A, B) => C): State[S, C] =
    this.flatMap((a) => sb.map(b => f(a, b)))

  def flatMap[B](f: A => State[S, B]): State[S, B] = State(s => {
    val (a, s3) = run(s)
    f(a) run s3
  })

  def sequence[A](fs: List[State[S, A]]): State[S, List[A]] =
    List.reduceRight(fs, State.unit[S, List[A]](List()))((u, t) =>
      u.flatMap(l => t.map(a => io.acari.Cons(l, a))))
}

sealed trait Input

case object Coin extends Input

case object Turn extends Input

case class Machine(locked: Boolean, candies: Int, coins: Int)

object State {
  type Rand[A] = State[RNG, A]

  def unit[S, A](a: A): State[S, A] =
    State(s => (a, s))

  //todo:this!
  def simulateMachine(inputs: List[Input]): State[Machine, (Int, Int)] = ???
}
