package com.navandres

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString

import scala.io.Source
import scala.util.Random

object TcpServer {
  def props(remote: InetSocketAddress) =
    Props(new TcpServer(remote))
}

class TcpServer(remote: InetSocketAddress) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, remote)

  def receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) ⇒ context stop self

    case c @ Connected(remote, local) =>
      println(s"Client connected - Remote(Client): ${remote.getAddress} Local(Server): ${local.getAddress}")
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)

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

  }

}