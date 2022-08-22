package git.infrastructure.filesystem

import git.domain.model.Hash
import git.domain.repository.{ObjectRepository, ObjectRepositoryError}
import zio.{IO, ZLayer}
import zio.stream.{ZPipeline, ZSink, ZStream}

object ObjectRepositoryFileSystem {

  extension (hash: Hash) {
    def getFilePath: String = {
      val directoryNumberOfChar = 2
      s"${hash.hash.take(directoryNumberOfChar)}/${hash.hash.drop(directoryNumberOfChar)}"
    }
  }

  def live(root: String) = ZLayer.succeed(new ObjectRepository {
    override def save(
        hash: Hash,
        byteStream: ZStream[Any, Throwable, Byte]
    ): IO[ObjectRepositoryError, Unit] = {
      byteStream
        .via(ZPipeline.deflate())
        .run(ZSink.fromFileName(s"$root/${hash.getFilePath}"))
        .mapError(error =>
          ObjectRepositoryError(
            "Error when saving a blob in the file system",
            error
          )
        )
        .unit
    }
  })

}
