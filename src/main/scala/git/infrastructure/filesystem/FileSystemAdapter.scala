package git.infrastructure.filesystem

import git.domain.model.FileIdentifier
import git.domain.port.FileSystemPort
import zio.ZLayer
import zio.stream.ZStream


object FileSystemAdapter {
  
  val live = ZLayer.succeed(new FileSystemPort {
    override def readFileBytes(file: FileIdentifier): ZStream[Any, Throwable, Byte] =
      ZStream.fromFileName(file.filepath)
  })
  
} 
