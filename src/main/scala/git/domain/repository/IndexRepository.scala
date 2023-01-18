package git.domain.repository

import git.domain.model.index.Index
import zio.ZIO

case class IndexRepositoryError(message: String) extends Exception(message) 

trait IndexRepository {
  def readIndex(): ZIO[Any, IndexRepositoryError, Index]
}
