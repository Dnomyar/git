package git.domain.model.index

case class Flags(
    assumeValid: Boolean,
    extended: Boolean,
    stage: (Boolean, Boolean),
    nameLength: Int
)
