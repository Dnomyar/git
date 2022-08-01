package git.domain.repository

import git.domain.model.Hash
import zio.stream.ZStream
import zio.{IO, Task}

case class ObjectRepositoryError(message:String, throwable: Throwable) extends Throwable(message, throwable)

trait ObjectRepository {

  def save(hash: Hash, byteStream: ZStream[Any, Throwable, Byte]): IO[ObjectRepositoryError, Unit]

}
