package git

import git.domain.port.FileSystemPort
import git.domain.usecase.HashObjectUseCase
import git.infrastructure.filesystem.FileSystemAdapter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.Console.*

import java.io.IOException

object MainSpec extends ZIOSpecDefault {

  case class ConsoleRecorder(printedLines: Chunk[String])
  private def provideMockConsole(recorder: Ref[ConsoleRecorder]): ULayer[Console] =
    ZLayer.succeed(new Console{
      override def print(line: => Any)(implicit trace: Trace): IO[IOException, Unit] = ???

      override def printError(line: => Any)(implicit trace: Trace): IO[IOException, Unit] = ???

      override def printLine(line: => Any)(implicit trace: Trace): IO[IOException, Unit] =
        recorder.update{
          case consoleRecorder@ConsoleRecorder(printedLines) =>
            consoleRecorder.copy(printedLines = printedLines.appended(line.toString))
        }

      override def printLineError(line: => Any)(implicit trace: Trace): IO[IOException, Unit] = ???

      override def readLine(implicit trace: Trace): IO[IOException, String] = ???
    })

  override def spec =
    suite("Main")(
      test("print the hash of the string parameter 'test content'"){
        for {
          recorder <- Ref.make(ConsoleRecorder(Chunk.empty))
          _ <- Main.program(Chunk("--text", "test content"))
            .provideSome[HashObjectUseCase.HashObjectUseCase](provideMockConsole(recorder))
          recorded <- recorder.get
        } yield assert(recorded.printedLines)(equalTo(Chunk(
          "08cf6101416f0ce0dda3c80e627f333854c4085c"
        )))
      },
      test("print the hash of a specified file name"){
        for {
          recorder <- Ref.make(ConsoleRecorder(Chunk.empty))
          _ <- Main.program(Chunk(
            "src/test/resources/hash-object-file1.txt",
            "src/test/resources/hash-object-file3.txt",
          )).provideSome[HashObjectUseCase.HashObjectUseCase](provideMockConsole(recorder))
          recorded <- recorder.get
        } yield assert(recorded.printedLines)(equalTo(Chunk(
          "e4515249a870dd1c983f41425d88b496b80daf62",
          "8f0d852d3717afbe31bb13f5c1e77df81bbc3e6e"
        )))
      }
    ).provide(
      FileSystemAdapter.live,
      HashObjectUseCase.live
    )


}

