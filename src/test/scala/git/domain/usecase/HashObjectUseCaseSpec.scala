package git.domain.usecase

import git.domain.model.*
import git.domain.port.FileSystemPort
import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase.{encoding, *}
import git.domain.repository.{ObjectRepository, ObjectRepositoryError}
import git.infrastructure.ObjectRepositoryMock
import git.infrastructure.ObjectRepositoryMock.*
import git.infrastructure.filesystem.FileSystemAdapter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.*
import zio.stream.ZStream
import zio.test.*
import zio.test.Assertion.*

object HashObjectUseCaseSpec extends ZIOSpecDefault {
  private val encoding = "UTF-8"
  override def spec =
    suite("Hash object usecase")(
      List(
        ("test content", "08cf6101416f0ce0dda3c80e627f333854c4085c"),
        ("éçß", "c5d47748db36b42f9fefd51f893a6accc00fd827"),
        ("", "e69de29bb2d1d6434b8b29ae775ad8c2e48c5391")
      ).flatMap { case (input, expectedHash) =>
        List(
          test(s"hash text '${input}'") {
            (for {
              hashObjectUseCase <- ZIO
                .service[HashObjectUseCase.HashObjectUseCase]
              hashObjectResult <- hashObjectUseCase.handleCommand(
                HashObjectCommand.HashText(textToHash = input)
              )
            } yield assert(hashObjectResult.hash)(
              equalTo(List(Hash(expectedHash)))
            ))
              .provide(
                ZLayer.succeed[FileSystemPort]((_: FileIdentifier) => ???),
                ZLayer.succeed[ObjectRepository](new ObjectRepository {
                  override def save(
                      hash: Hash,
                      byteStream: ZStream[Any, Throwable, Byte]
                  ): IO[ObjectRepositoryError, Unit] = ZIO.unit
                }),
                HashObjectUseCase.live
              )
          },
          test(s"hash file content '${input}'") {
            (for {
              hashObjectUseCase <- ZIO
                .service[HashObjectUseCase.HashObjectUseCase]
              hashObjectResult <- hashObjectUseCase.handleCommand(
                HashObjectCommand.HashFile(filenames = List(FileIdentifier("")))
              )
            } yield assert(hashObjectResult.hash)(
              equalTo(List(Hash(expectedHash)))
            ))
              .provide(
                ZLayer.succeed[FileSystemPort]((_: FileIdentifier) =>
                  ZStream.fromIterable(input.getBytes(encoding))
                ),
                ZLayer.succeed[ObjectRepository](new ObjectRepository {
                  override def save(
                      hash: Hash,
                      byteStream: ZStream[Any, Throwable, Byte]
                  ): IO[ObjectRepositoryError, Unit] = ZIO.unit
                }),
                HashObjectUseCase.live
              )
          },
          test(
            s"should call the object repository when specified for the command HashFile - for file content $input"
          ) {
            val fileMap = Map(
              FileIdentifier("input") -> ZStream.fromIterable(
                input.getBytes(encoding)
              ),
              FileIdentifier("empty") -> ZStream.fromIterable(List.empty)
            )
            for {
              registry <- ObjectRepositoryMock.initRegistry
              hashObjectUseCase <- ZIO
                .service[HashObjectUseCase.HashObjectUseCase]
                .provide(
                  ZLayer.succeed[FileSystemPort]((file: FileIdentifier) =>
                    fileMap(file)
                  ),
                  ObjectRepositoryMock.objectRepository(registry),
                  HashObjectUseCase.live
                )

              hashObjectResult <- hashObjectUseCase.handleCommand(
                HashObjectCommand.HashFile(
                  filenames =
                    List(FileIdentifier("input"), FileIdentifier("empty")),
                  save = true
                )
              )
              objectRepositoryMockEvents <- registry.get
            } yield assert(objectRepositoryMockEvents)(
              hasSameElements(
                Vector(
                  ObjectRepositoryMockEvent.Save(
                    Hash(expectedHash),
                    Chunk
                      .from(
                        input.getBytes(encoding)
                      )
                      .prefixWithBlobSizeAndZeroByte
                  ),
                  ObjectRepositoryMockEvent.Save(
                    Hash("e69de29bb2d1d6434b8b29ae775ad8c2e48c5391"),
                    Chunk.from(List.empty).prefixWithBlobSizeAndZeroByte
                  )
                )
              )
            ) && assert(hashObjectResult.hash)(
              equalTo(
                List(
                  Hash(expectedHash),
                  Hash("e69de29bb2d1d6434b8b29ae775ad8c2e48c5391")
                )
              )
            )
          }
        )
      }
    )

  extension (chunk: Chunk[Byte]) {
    private def prefixWithBlobSizeAndZeroByte: Chunk[Byte] = {
      val zeroByte = '\u0000'
      val length = chunk.length
      Chunk.from(s"blob $length$zeroByte".getBytes(encoding)) ++ chunk
    }
  }
}
