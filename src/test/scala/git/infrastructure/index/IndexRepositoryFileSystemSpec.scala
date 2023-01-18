package git.infrastructure.index

import git.domain.repository.IndexRepository
import zio.test.{Spec, ZIOSpecDefault}
import zio.*
import zio.test.*
import zio.test.Assertion.*
import git.infrastructure.index.IndexRepositoryFileSystem

object IndexRepositoryFileSystemSpec extends ZIOSpecDefault{
  override def spec: Spec[Any, Any] =
    test("read the index file") {
      for {
        indexRepositoryFileSystem <- ZIO.service[IndexRepository]
        index <- indexRepositoryFileSystem.readIndex()
      } yield assert(index.entries.map(_.name))(equalTo(List("README.md")))
    }.provide(
      IndexBinaryDecoder.live,
      IndexRepositoryFileSystem.live("src/test/resources/index-file-simple-1")
    )
}
