package git.domain.usecase

import java.security.MessageDigest
import zio.*
import zio.stream.{Sink, *}
import git.domain.*
import git.domain.model.FileIdentifier
import git.domain.port.FileSystemPort

object HashObjectUseCase {

  enum HashObjectCommand {
    case HashText(textToHash: String)
    case HashFile(filenames: List[FileIdentifier])
  }

  case class HashObjectResult(hash: List[String])

  private val encoding = "UTF-8"

  trait HashObjectUseCase {
    def handleCommand(
        hashObjectCommand: HashObjectCommand
    ): Task[HashObjectResult]
  }

  class HashObjectUseCaseImplementation(fileSystemPort: FileSystemPort)
      extends HashObjectUseCase {
    override def handleCommand(
        hashObjectCommand: HashObjectCommand
    ): Task[HashObjectResult] =
      hashObjectCommand match {
        case HashObjectCommand.HashText(textToHash) =>
          hashByteStream(ZStream.fromIterable(textToHash.getBytes(encoding)))
            .map(hash => HashObjectResult(List(hash)))
        case HashObjectCommand.HashFile(files) =>
          ZIO
            .foreachPar(files)(file =>
              hashByteStream(fileSystemPort.readFileBytes(file))
            )
            .map(hashes => HashObjectResult(hashes))
      }

    private def hashByteStream(byteStream: ZStream[Any, Throwable, Byte]) = {
      for {
        numberOfBytes <- byteStream.run(ZSink.count)
        hash <-
          ZStream
            .fromIterable(providePrefixBytes(numberOfBytes))
            .concat(byteStream)
            .run(SinkExtension.sha1Sink)
      } yield hash
    }

    private def providePrefixBytes(length: Long) = {
      val zeroByte = '\u0000'

      s"blob $length$zeroByte".getBytes(encoding)
    }
  }

  val live = ZLayer.fromFunction((fileSystemPort: FileSystemPort) =>
    new HashObjectUseCaseImplementation(fileSystemPort)
  )

}
