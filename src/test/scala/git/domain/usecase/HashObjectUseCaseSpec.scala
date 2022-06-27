package git.domain.usecase

import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio._
import zio.test._
import zio.test.Assertion._

object HashObjectUseCaseSpec extends ZIOSpecDefault {

  override def spec =
    suite("Hash object usecase")(
      test("hash 'test content'"){
        for {
          hash <- HashObjectUseCase.handleCommand(HashObjectCommand(strToHash = "test content"))
        } yield assert(hash)(equalTo("08cf6101416f0ce0dda3c80e627f333854c4085c"))
      }
    )
}

object HashObjectUseCaseSpecProxy extends ZIOApp.Proxy(HashObjectUseCaseSpec)
