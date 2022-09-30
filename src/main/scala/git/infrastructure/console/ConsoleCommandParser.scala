package git.infrastructure.console

import zio.Chunk
import git.domain.usecase.HashObjectUseCase.*
import git.domain.model.FileIdentifier

object ConsoleCommandParser {

  def parseCommand(args: Chunk[String]) = {
    args.toList match {
      case "--text" :: text :: Nil =>
        HashObjectCommand.HashText(text)
      case "--save" :: filenames =>
        HashObjectCommand.HashFile(
          filenames.map(FileIdentifier.apply),
          save = true
        )
      case filenames =>
        HashObjectCommand.HashFile(
          filenames.map(FileIdentifier.apply)
        )
    }
  }

}
