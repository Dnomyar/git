package git.infrastructure.filesystem

import git.domain.repository.ObjectRepository
import git.domain.model.Hash
import org.scalatest.flatspec.AnyFlatSpecLike
import zio.ZIO
import zio.stream.{ZPipeline, ZStream}
import zio.test.*
import zio.test.Assertion.*

import java.nio.file.*

object ObjectRepositoryFileSystemSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, Any] =
    suite("ObjectRepositoryFileSystem should")(
      test("save the blob at the right place in the right format") {
        val gitObjectRepositoryRoot = "target/gitobjects"
        (for {
          repo <- ZIO.service[ObjectRepository]
          fileToSave = s"""blob 41${'\u0000'}version = "3.5.3"
                         |runner.dialect = scala3""".stripMargin
          nameOfGeneratedFile =
            s"$gitObjectRepositoryRoot/8c/d96400238da79f664f70654ef1b65630c93afa"
          _ <- ZIO.attempt(Files.deleteIfExists(Paths.get(nameOfGeneratedFile)))
          _ <- repo.save(
            Hash("8cd96400238da79f664f70654ef1b65630c93afa"),
            ZStream.fromIterable(fileToSave.getBytes("UTF-8"))
          )
          expectedFile <- ZStream
            .fromFileName(
              s"src/test/resources/blob-8cd96400238da79f664f70654ef1b65630c93afa"
            )
            .via(ZPipeline.inflate())
            .runCollect
          fileGenerated <- ZStream
            .fromFileName(nameOfGeneratedFile)
            .via(ZPipeline.inflate())
            .runCollect
        } yield assert(fileGenerated)(equalTo(expectedFile)))
          .provide(ObjectRepositoryFileSystem.live(gitObjectRepositoryRoot))
      }
    )
}
