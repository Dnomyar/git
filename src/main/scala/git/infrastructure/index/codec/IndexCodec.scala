package git.infrastructure.index.codec

import git.domain.model.index.{Entry, Index}
import scodec.*
import scodec.bits.*
import scodec.codecs.*

val indexCodec: Codec[Index] = {
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
