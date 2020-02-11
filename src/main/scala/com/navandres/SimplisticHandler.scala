package com.navandres

import akka.actor.Actor
import akka.io.Tcp

class SimplisticHandler extends Actor {
  import Tcp._
  def receive = {
    case Received(data) =>
      println(s"Data received - ${data.utf8String}")
    case PeerClosed     => context stop self
  }

}