package git.infrastructure.index

import git.domain.model.index.Index
import git.domain.repository.{IndexRepository, IndexRepositoryError}
import zio.stream.ZStream
import zio.{ZIO, ZLayer}

object IndexRepositoryFileSystem {

  def live(pathToIndexFile: String) =
    ZLayer.fromFunction((indexBinaryDecoder: IndexBinaryDecoder) =>
      new IndexRepository {
        override def readIndex(): ZIO[Any, IndexRepositoryError, Index] =
          for {
            fileBytes <- ZStream
              .fromFileName(pathToIndexFile)
              .runCollect
              .mapError(error => IndexRepositoryError(error.getMessage))
            decoded <- indexBinaryDecoder.decode(fileBytes.toArray)
          } yield decoded
      }
    )

}
