package org.deeplearning4j.scalphagozero.app

/**
  * If "gtp" option is passed, then acts as GTP client,
  * otherwise does the regular DemoApp
  *
  * @author Max Pumperla
  * @author Barry Becker
  */
object ScalphaGoZero extends App {

  println("args = " + args.mkString(", "))
  if (args.length == 0) {
    new Demo().run()
  } else if (args(0) == "gtp") {
    new GtpClient().run()
  }

}
