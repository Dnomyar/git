package git

import git.domain.*
import git.domain.model.*
import git.domain.repository.ObjectRepository
import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase.*
import git.infrastructure.filesystem.FileSystemAdapter
import zio.*
import zio.Console.*

object Main extends ZIOAppDefault {
  override def run =
    (for {
      args <- getArgs
      _ <- program(args)
        .provide(
          ZLayer.succeed(ConsoleLive),
          FileSystemAdapter.live,
          ZLayer.succeed[ObjectRepository](???),
          HashObjectUseCase.live
        )
    } yield ()).exitCode

  def program(args: Chunk[String]) = for {
    console <- ZIO.service[Console]
    hashObjectUsecase <- ZIO.service[HashObjectUseCase.HashObjectUseCase]
    hash <- args.toList match {
      case "--text" :: text :: Nil =>
        hashObjectUsecase.handleCommand(HashObjectCommand.HashText(text))
      case filenames =>
        hashObjectUsecase.handleCommand(
          HashObjectCommand.HashFile(
            filenames.map(FileIdentifier.apply)
          )
        )
    }
    _ <- ZIO.foreach(hash.hash)(console.printLine(_))
  } yield ()
}
