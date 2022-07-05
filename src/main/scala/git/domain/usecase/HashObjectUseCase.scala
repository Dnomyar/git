package git.domain.usecase

import java.security.MessageDigest
import zio._
import zio.stream.*

object HashObjectUseCase {

  case class HashObjectCommand(strToHash: String)

  private val encoding = "UTF-8"

  def handleCommand(hashObjectCommand: HashObjectCommand): Task[String] = {
    val byteStream: ZStream[Any, Throwable, Byte] = ZStream.fromIterable(hashObjectCommand.strToHash.getBytes(encoding))

    val sha1Sink = for {
      digest <- ZSink.foldLeftChunks[Byte, MessageDigest](MessageDigest.getInstance("SHA-1")) {
        case (digest, bytes) =>
          digest.update(bytes.toArray)
          digest
      }
    } yield digest.digest.map("%02x".format(_)).mkString

    for {
      numberOfBytes <- byteStream.run(ZSink.count)
      hash <-
        ZStream.fromIterable(providePrefixBytes(numberOfBytes))
          .concat(byteStream)
          .run(sha1Sink)
    } yield hash
  }

  private def providePrefixBytes(length: Long) = {
    val zeroByte = '\u0000'

    s"blob $length$zeroByte".getBytes(encoding)
  }

}
