package com.navandres

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString

object Main {
  def main(args: Array[String]): Unit = {
    val host = "0.0.0.0"
    val port = 9999
    println(s"Server started! listening to ${host}:${port}")

    val serverProps = Props(classOf[TcpServer], new InetSocketAddress(host, port))
    val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")
    val serverActor: ActorRef = actorSystem.actorOf(serverProps)
    serverActor ! ByteString("Starting server...")
  }
}
