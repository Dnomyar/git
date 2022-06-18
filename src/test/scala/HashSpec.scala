import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HashSpec extends AnyFlatSpec with Matchers {

  behavior of "hash"

  it should "hash 'test content'" in {
    hash("test content") should be("08cf6101416f0ce0dda3c80e627f333854c4085c")
  }

  behavior of "sha1"

  it should "sha1 'test content'" in {
    sha1("test content") should be("1eebdf4fdc9fc7bf283031b93f9aef3338de9052")
  }

}
