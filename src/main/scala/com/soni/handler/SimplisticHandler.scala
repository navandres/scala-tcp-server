package com.soni.handler

import akka.actor.Actor
import akka.io.Tcp
import akka.util.ByteString

import scala.io.Source
import scala.util.Random

class SimplisticHandler extends Actor {
  import Tcp._
  def receive = {
    case Received(data) =>
      println(s"Data received - ${data.utf8String}")

      // val lines = Source.fromResource("access_log.txt").getLines
      val stream = getClass.getResourceAsStream("/access_log.txt")

      val lines = Source.fromInputStream( stream ).getLines.toArray

      import scala.collection.immutable.Stream.continually

      // partially applied function with the random producer seeded
      val random = new Random()
      def randomLine(random: Random, lines: Array[String]): String = {
        lines( random.nextInt(lines.length) )
      }

      for(line <- continually(randomLine(random, lines))) {
        println(line)
        sender() ! Write(ByteString(line))
      }

    case PeerClosed     => context stop self
  }

}