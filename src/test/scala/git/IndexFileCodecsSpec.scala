package git

import git.domain.model.index.*
import zio.stream.ZStream
import zio.test.*
import zio.test.Assertion.*

import java.math.BigInteger

object IndexFileCodecsSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, Any] =
    test("decode a simple index file") {
      for {
        fileBytes <- ZStream
          .fromFileName("src/test/resources/index-file-simple-1")
          .runCollect
        headersAndEntry = fileBytes.take(83)
        result = {
          import scodec.*
          import scodec.bits.*
          import scodec.codecs.*
          val flags: Codec[Flags] = (("assume-valid" | bool) ::
            ("extended" | bool) ::
            ("stage" | (bool :: bool)) ::
            ("name-length" | uint(12))).xmap(
            Flags(_, _, _, _),
            flags =>
              (flags.assumeValid, flags.extended, flags.stage, flags.nameLength)
          )

          val codexEntry: Codec[Entry] = (("ctime-seconds" | uint32) ::
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
            ("flags" | flags))
            .consume[Entry](
              (
                  ctimeSeconds,
                  ctimeNanosecondFractions,
                  mtimeSeconds,
                  mtimeNanosecondFractions,
                  dev,
                  ino,
                  mode,
                  uid,
                  gid,
                  size,
                  sha1,
                  flags
              ) => {
                ("pathName" | variableSizeBytes(
                  provide(flags.nameLength),
                  utf8
                )).xmap[Entry](
                  path =>
                    Entry(
                      ctimeSeconds,
                      ctimeNanosecondFractions,
                      mtimeSeconds,
                      mtimeNanosecondFractions,
                      dev,
                      ino,
                      mode,
                      uid,
                      gid,
                      size,
                      sha1.toArray,
                      flags,
                      path
                    ),
                  entry => entry.name
                )
              }
            ) {
              case Entry(
                    ctimeSeconds,
                    ctimeNanosecondFractions,
                    mtimeSeconds,
                    mtimeNanosecondFractions,
                    dev,
                    ino,
                    mode,
                    uid,
                    gid,
                    size,
                    sha1,
                    flags,
                    _
                  ) =>
                (
                  ctimeSeconds,
                  ctimeNanosecondFractions,
                  mtimeSeconds,
                  mtimeNanosecondFractions,
                  dev,
                  ino,
                  mode,
                  uid,
                  gid,
                  size,
                  ByteVector(sha1),
                  flags
                )
            }

          val headerCodec: Codec[Index] = {
            (("header.signature" | constant(BitVector("DIRC".getBytes))) ~>
              ("header.version" | uint32) ::
              ("number-of-entries" | uint32)).flatZip[Entry]((_, _) =>
              codexEntry
            )
          }.xmap[Index](
            { case ((version, _), entry) => Index(version, List(entry)) },
            { case Index(version, entries) =>
              ((version, entries.size), entries.head)
            }
          )

          val reencoded: Attempt[BitVector] = for {
            decoded <- headerCodec.decode(BitVector(headersAndEntry))
            reencoded <- headerCodec.encode(decoded.value)
          } yield reencoded
          reencoded.require.toByteArray
        }
      } yield assert(headersAndEntry.toArray)(equalTo(result))
    }

}
