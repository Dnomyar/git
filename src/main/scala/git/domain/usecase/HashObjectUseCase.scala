package git.domain.usecase

import java.security.MessageDigest
import zio.*
import zio.stream.{Sink, *}
import git.domain.*
import git.domain.model.{FileIdentifier, Hash}
import git.domain.port.FileSystemPort
import git.domain.repository.ObjectRepository

object HashObjectUseCase {

  enum HashObjectCommand {
    case HashText(textToHash: String)
    case HashFile(filenames: List[FileIdentifier], save: Boolean = false)
  }

  case class HashObjectResult(hash: List[Hash])

  private val encoding = "UTF-8"

  trait HashObjectUseCase {
    def handleCommand(
        hashObjectCommand: HashObjectCommand
    ): Task[HashObjectResult]
  }

  val live = ZLayer.fromFunction(
    (fileSystemPort: FileSystemPort, objectRepository: ObjectRepository) =>
      new HashObjectUseCase {
        override def handleCommand(
            hashObjectCommand: HashObjectCommand
        ): Task[HashObjectResult] =
          hashObjectCommand match {
            case HashObjectCommand.HashText(textToHash) =>
              hashByteStream(
                ZStream.fromIterable(textToHash.getBytes(encoding))
              )
                .map { case (hash, _) =>
                  HashObjectResult(List(hash))
                }
            case HashObjectCommand.HashFile(files, _) =>
              ZIO
                .foreachPar(files) { file =>
                  val bytes = fileSystemPort.readFileBytes(file)
                  for {
                    hashAndPrefixedStream <- hashByteStream(bytes)
                    (hash, prefixedStream) = hashAndPrefixedStream
                    _ <- objectRepository.save(hash, prefixedStream)
                  } yield hash
                }
                .map(hashes => HashObjectResult(hashes))
          }

        private def hashByteStream(
            byteStream: ZStream[Any, Throwable, Byte]
        ) = {
          for {
            numberOfBytes <- byteStream.run(ZSink.count)
            prefixedStream = ZStream
              .fromIterable(providePrefixBytes(numberOfBytes))
              .concat(byteStream)
            hash <-
              prefixedStream
                .run(SinkExtension.sha1Sink)
          } yield (Hash(hash), prefixedStream)
        }

        private def providePrefixBytes(length: Long) = {
          val zeroByte = '\u0000'

          s"blob $length$zeroByte".getBytes(encoding)
        }
      }
  )

}
