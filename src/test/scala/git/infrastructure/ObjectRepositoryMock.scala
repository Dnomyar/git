package git.infrastructure

import git.domain.model.Hash
import git.domain.repository.{ObjectRepository, ObjectRepositoryError}
import zio.stream.ZStream
import zio.Chunk
import zio.{IO, Ref, UIO, ULayer, ZLayer}

object ObjectRepositoryMock {

  enum ObjectRepositoryMockEvent {
    case Save(hash: Hash, byteStream: Chunk[Byte])
  }

  def initRegistry: UIO[Ref[Vector[ObjectRepositoryMockEvent]]] = Ref.make(Vector.empty[ObjectRepositoryMockEvent])

  def objectRepository(registry: Ref[Vector[ObjectRepositoryMockEvent]]): ULayer[ObjectRepository] =
    ZLayer.succeed(new ObjectRepository {
      override def save(
          hash: Hash,
          byteStream: ZStream[Any, Throwable, Byte]
      ): IO[ObjectRepositoryError, Unit] = for{
        bytes <- byteStream.runCollect.mapError(error => ObjectRepositoryError("error while collecting bytes in the mock", error))
        _ <- registry.update(_.appended(ObjectRepositoryMockEvent.Save(hash, bytes)))
      } yield ()
    })

}
