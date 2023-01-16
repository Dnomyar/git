package git

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
        _ = {
          import scodec.*
          import scodec.bits.*
          import scodec.codecs.*
          case class Flags(
              assumeValid: Boolean,
              extended: Boolean,
              stage: (Boolean, Boolean),
              nameLength: Int
          )
          case class Entry(
              ctime: Long,
              ctimeNanoseconds: Long,
              mtime: Long,
              mtimeNanoseconds: Long,
              dev: Long,
              ino: Long,
              mode: Long,
              uid: Long,
              gid: Long,
              size: Long,
              sha: Array[Byte],
              flags: Flags,
              name: String
          )
          val flags: Codec[Flags] = (("assume-valid" | bool) ::
            ("extended" | bool) ::
            ("stage" | (bool :: bool)) ::
            ("name-length" | uint(12))).xmap(Flags(_, _, _, _), _ => ???)

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
                  _ => ???
                )
              },
            )( _ => ???)

          val headerCodec = {
            (("header.signature" | constant(BitVector("DIRC".getBytes))) ::
              ("header.version" | uint32) ::
              ("number-of-entries" | uint32)).flatZip[Entry](
              (_, version, numberOfEntries) => codexEntry
            )
          }

          println(
            headerCodec
              .decode(
                BitVector(fileBytes.toArray)
              )
//              .require
//              .value
          )
        }
      } yield assert(true)(isTrue)
    }

}
