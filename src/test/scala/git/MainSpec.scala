package git

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class MainSpec extends AnyFlatSpec with Matchers {

  it should "println the hash of the string parameter" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      main("test content")
    }
    stream.toString should be ("08cf6101416f0ce0dda3c80e627f333854c4085c\n")
  }

}
