package git.infrastructure.index.codec

import git.domain.model.index.Entry
import scodec.*
import scodec.bits.*
import scodec.codecs.*

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
