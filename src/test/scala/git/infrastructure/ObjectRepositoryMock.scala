package git.infrastructure

import git.domain.model.Hash
import git.domain.repository.{ObjectRepository, ObjectRepositoryError}
import zio.stream.ZStream
import zio.{IO, Ref, UIO, ULayer, ZLayer}

object ObjectRepositoryMock {

  enum ObjectRepositoryMockEvent {
    case Save(hash: Hash, byteStream: ZStream[Any, Throwable, Byte])
  }

  def initRegistry: UIO[Ref[Vector[ObjectRepositoryMockEvent]]] = Ref.make(Vector.empty[ObjectRepositoryMockEvent])

  def objectRepository(registry: Ref[Vector[ObjectRepositoryMockEvent]]): ULayer[ObjectRepository] =
    ZLayer.succeed(new ObjectRepository {
      override def save(
          hash: Hash,
          byteStream: ZStream[Any, Throwable, Byte]
      ): IO[ObjectRepositoryError, Unit] =
        registry.update(_.appended(ObjectRepositoryMockEvent.Save(hash, byteStream)))
    })

}
