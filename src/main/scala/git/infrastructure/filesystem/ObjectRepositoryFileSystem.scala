package git.infrastructure.filesystem

import git.domain.model.Hash
import git.domain.repository.{ObjectRepository, ObjectRepositoryError}
import zio.{IO, ZLayer}
import zio.stream.ZStream

object ObjectRepositoryFileSystem {
  
  def live(root: String) = ZLayer.succeed(new ObjectRepository{
    override def save(hash: Hash, byteStream: ZStream[Any, Throwable, Byte]): IO[ObjectRepositoryError, Unit] = ???
  })

}
