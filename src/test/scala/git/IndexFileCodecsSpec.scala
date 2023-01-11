package git

import zio.stream.ZStream
import zio.test.*
import zio.test.Assertion.*

import java.math.BigInteger

object ParseIndexSpecExperiment2 extends ZIOSpecDefault {
  override def spec: Spec[Any, Any] =
    test("parse index") {
      for {
        fileBytes <- ZStream.fromFileName(".git/index").runCollect
        _ = {
          import scodec.*
          import scodec.bits.*
          import scodec.codecs.*

          val headerCodec =
            (("header.signature" | constant(BitVector("DIRC".getBytes))) ::
              ("header.version" | uint32) ::
              ("number-of-entries" | uint32)).flatPrepend(
              (_, version, numberOfEntries) =>
                ("ctime-seconds" | uint32) ::
                  ("ctime-nanosecond-fractions" | uint32) ::
                  ("mtime-seconds" | uint32) ::
                  ("mtime-nanosecond-fractions" | uint32) ::
                  ("dev" | uint32) ::
                  ("ino" | uint32) ::
                  ("mode" | uint32) :: //
                  ("uid" | uint32) ::
                  ("gid" | uint32) ::
                  ("size" | uint32) ::
                  ("sha1" | bytes(20)) ::
                  ("flags" | uint16) //:: ("path" | variableSizeBytes(uint16, utf8))
            )

          println(
            headerCodec.decode(
              BitVector(fileBytes.toArray)
            ).require.value
          )
          println(fileBytes.length)
        }
      } yield assert(true)(isTrue)
    }
}
