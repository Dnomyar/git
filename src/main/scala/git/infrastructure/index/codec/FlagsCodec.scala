package git.infrastructure.index.codec

import git.domain.model.index.Flags
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

