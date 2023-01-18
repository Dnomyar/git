package git.infrastructure.index

import git.domain.model.index.Index
import git.domain.repository.{IndexRepository, IndexRepositoryError}
import zio.ZIO

object IndexRepositoryFileSystem {

  val live = new IndexRepository{
    override def readIndex(): ZIO[Any, IndexRepositoryError, Index] = ???
  }
  
}
