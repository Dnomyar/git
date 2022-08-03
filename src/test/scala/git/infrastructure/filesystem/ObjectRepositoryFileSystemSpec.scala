package git.infrastructure.filesystem

import git.domain.repository.ObjectRepository
import git.domain.model.Hash
import org.scalatest.flatspec.AnyFlatSpecLike
import zio.ZIO
import zio.stream.ZStream
import zio.test.*
import zio.test.Assertion.*

object ObjectRepositoryFileSystemSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, Any] =
    suite("ObjectRepositoryFileSystem should")(
      test("save the blob at the right place in the right format") {
        val gitObjectRepositoryRoot = "target/gitobjects"
        (for {
          repo <- ZIO.service[ObjectRepository]
          fileToSave = """blob 41version = "3.5.3"
                         |runner.dialect = scala3""".stripMargin
          _ <- repo.save(
            Hash("8cd96400238da79f664f70654ef1b65630c93afa"),
            ZStream.fromIterable(fileToSave.getBytes("UTF-8"))
          )
          expectedFile <- ZStream.fromFileName(s"src/test/resources/blob-8cd96400238da79f664f70654ef1b65630c93afa").runCollect
          fileWritten <- ZStream.fromFileName(s"$gitObjectRepositoryRoot/8c/d96400238da79f664f70654ef1b65630c93afa").runCollect
        } yield assert(fileWritten)(equalTo(expectedFile)))
          .provide(ObjectRepositoryFileSystem.live(gitObjectRepositoryRoot))
      }
    )
}
