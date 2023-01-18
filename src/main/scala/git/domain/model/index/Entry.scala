package git.domain.model.index

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
