package git.infrastructure.index

import git.domain.model.index.Index
import git.domain.repository.IndexRepositoryError
import git.infrastructure.index.codec.indexCodec
import scodec.Attempt
import scodec.bits.BitVector
import zio.{ZIO, ZLayer}

trait IndexBinaryDecoder {

  def decode(bytes: Array[Byte]): ZIO[Any, IndexRepositoryError, Index]

}

object IndexBinaryDecoder {
  val live = ZLayer.succeed(new IndexBinaryDecoder {
    override def decode(bytes: Array[Byte]): ZIO[Any, IndexRepositoryError, Index] =
      indexCodec.decode(BitVector(bytes)) match
        case Attempt.Successful(value) => ZIO.succeed(value.value)
        case Attempt.Failure(cause) => ZIO.fail(IndexRepositoryError(cause.messageWithContext))
  })
}
