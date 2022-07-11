package git

import git.domain.*
import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase._
import zio._
import zio.Console._

object Main extends ZIOAppDefault {
  override def run =
    (for {
      args <- getArgs
      _ <- program(args).provide(ZLayer.succeed(ConsoleLive))
    } yield ()).exitCode

  def program(args: Chunk[String]) = for {
    console <- ZIO.service[Console]
    hash <- args.toList match {
      case "--text" :: text :: Nil =>
        HashObjectUseCase.handleCommand(HashObjectCommand.HashText(text))
      case filenames =>
        HashObjectUseCase.handleCommand(HashObjectCommand.HashFile(filenames))
    }
    _ <- ZIO.foreach(hash.hash)(console.printLine(_))
  } yield ()
}
