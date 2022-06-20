package git.domain.usecase

import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HashObjectUseCaseSpec extends AnyFlatSpec with Matchers {

  behavior of "HashObjectUseCase"

  it should "hash 'test content'" in {
    HashObjectUseCase.handleCommand(HashObjectCommand(strToHash = "test content")) should be(
      "08cf6101416f0ce0dda3c80e627f333854c4085c"
    )
  }

}
