package git

import git.domain.*
import git.domain.model.*
import git.domain.repository.ObjectRepository
import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase.*
import git.infrastructure.filesystem.{
  FileSystemAdapter,
  ObjectRepositoryFileSystem
}
import git.infrastructure.console.ConsoleCommandParser
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
          ObjectRepositoryFileSystem.live(".git/objects"),
          HashObjectUseCase.live
        )
    } yield ()).exitCode

  def program(args: Chunk[String]) = for {
    console <- ZIO.service[Console]
    hashObjectUsecase <- ZIO.service[HashObjectUseCase.HashObjectUseCase]
    command = ConsoleCommandParser.parseCommand(args)
    hash <- hashObjectUsecase.handleCommand(command)
    _ <- ZIO.foreach(hash.hash)(console.printLine(_))
  } yield ()

}
