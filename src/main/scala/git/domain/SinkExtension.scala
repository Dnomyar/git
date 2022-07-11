package git.domain

import zio.stream.ZSink

import java.security.MessageDigest

object SinkExtension {

  val sha1Sink: ZSink[Any, Nothing, Byte, Nothing, String] = for {
    digest <- ZSink.foldLeftChunks[Byte, MessageDigest](MessageDigest.getInstance("SHA-1")) {
      case (digest, bytes) =>
        digest.update(bytes.toArray)
        digest
    }
  } yield digest.digest.map("%02x".format(_)).mkString

}
