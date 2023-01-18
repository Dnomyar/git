package git

import git.domain.model.index.*
import git.infrastructure.index.IndexBinaryDecoder
import git.infrastructure.index.codec.indexCodec
import zio.ZIO
import zio.stream.ZStream
import zio.test.*
import zio.test.Assertion.*

import java.math.BigInteger

object IndexFileCodecsSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, Any] = 
    test("decode and re-encode a simple index file") {
      for {
        fileBytes <- ZStream
          .fromFileName("src/test/resources/index-file-simple-1")
          .runCollect
        decoder <- ZIO.service[IndexBinaryDecoder]
        headersAndEntry = fileBytes.take(83)
        reencoded <- {
          import scodec.*
          import scodec.bits.*
          import scodec.codecs.*

          for {
            decoded <- decoder.decode(headersAndEntry.toArray)
            reencoded <- ZIO.succeed(
              indexCodec.encode(decoded).require.toByteArray
            )
          } yield reencoded
        }
      } yield assert(headersAndEntry.toArray)(equalTo(reencoded))
    }.provide(IndexBinaryDecoder.live)

}
