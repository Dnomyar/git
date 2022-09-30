package git.infrastructure.console

import git.domain.model.FileIdentifier
import git.domain.usecase.HashObjectUseCase.HashObjectCommand
import zio.Chunk
import zio.test.Assertion.*
import zio.test.*

object ConsoleCommandParserSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, Any] =
    suite("Console command parser")(
      test("should extract a hash text command") {
        val extractedCommand =
          ConsoleCommandParser.parseCommand(Chunk("--text", "test content"))
        assert(extractedCommand)(
          equalTo(
            HashObjectCommand.HashText("test content")
          )
        )
      },
      test("should extract a hash file command") {
        val extractedCommand =
          ConsoleCommandParser.parseCommand(
            Chunk("src/scala/Main.scala", "README.md")
          )
        assert(extractedCommand)(
          equalTo(
            HashObjectCommand.HashFile(
              List(
                FileIdentifier("src/scala/Main.scala"),
                FileIdentifier("README.md")
              ),
              save = false
            )
          )
        )
      },
      test("should extract a hash file command with save option") {
        val extractedCommand =
          ConsoleCommandParser.parseCommand(
            Chunk("--save", "src/scala/Main.scala", "README.md")
          )
        assert(extractedCommand)(
          equalTo(
            HashObjectCommand.HashFile(
              List(
                FileIdentifier("src/scala/Main.scala"),
                FileIdentifier("README.md")
              ),
              save = true
            )
          )
        )
      }
    )
}
