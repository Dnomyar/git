package git.domain.repository

import git.domain.model.Hash
import zio.stream.ZStream
import zio.{IO, Task}

trait ObjectRepositoryError extends Throwable

trait ObjectRepository {

  def save(hash: Hash, byteStream: ZStream[Any, Throwable, Byte]): IO[ObjectRepositoryError, Unit]

}
