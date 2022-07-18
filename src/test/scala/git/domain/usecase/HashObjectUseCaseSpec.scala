package git.domain.usecase

import git.domain.model.FileIdentifier
import git.domain.port.FileSystemPort
import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase.*
import git.infrastructure.filesystem.FileSystemAdapter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.*
import zio.stream.ZStream
import zio.test.*
import zio.test.Assertion.*

object HashObjectUseCaseSpec extends ZIOSpecDefault {

  override def spec =
    suite("Hash object usecase")(
      test("hash 'test content'") {
        for {
          hashObjectUseCase <- ZIO.service[HashObjectUseCase.HashObjectUseCase]
          hashObjectResult <- hashObjectUseCase.handleCommand(HashObjectCommand.HashText(textToHash = "test content"))
        } yield assert(hashObjectResult.hash)(equalTo(List("08cf6101416f0ce0dda3c80e627f333854c4085c")))
      },
      test("hash 'éçß'") {
        for {
          hashObjectUseCase <- ZIO.service[HashObjectUseCase.HashObjectUseCase]
          hashObjectResult <- hashObjectUseCase.handleCommand(HashObjectCommand.HashText(textToHash = "éçß"))
        } yield assert(hashObjectResult.hash)(equalTo(List("c5d47748db36b42f9fefd51f893a6accc00fd827")))
      },
      test("hash ''") {
        for {
          hashObjectUseCase <- ZIO.service[HashObjectUseCase.HashObjectUseCase]
          hashObjectResult <- hashObjectUseCase.handleCommand(HashObjectCommand.HashText(textToHash = ""))
        } yield assert(hashObjectResult.hash)(equalTo(List("e69de29bb2d1d6434b8b29ae775ad8c2e48c5391")))
      }
    ).provide(
      ZLayer.succeed[FileSystemPort]((file: FileIdentifier) => ???),
      HashObjectUseCase.live
    )
}

