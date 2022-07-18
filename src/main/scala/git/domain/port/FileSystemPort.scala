package git.domain.port

import git.domain.model.FileIdentifier
import zio.stream.ZStream

trait FileSystemPort {

  def readFileBytes(file: FileIdentifier): ZStream[Any, Throwable, Byte]
  
}
