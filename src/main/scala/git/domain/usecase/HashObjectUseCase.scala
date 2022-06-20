package git.domain.usecase

import java.security.MessageDigest

object HashObjectUseCase {
  
  case class HashObjectCommand(strToHash: String)
  
  def handleCommand(hashObjectCommand: HashObjectCommand): String = {
    hash(hashObjectCommand.strToHash)
  }

  private def hash(str: String): String = {
    val zeroByte = '\u0000'
    sha1(s"blob ${str.length}${zeroByte}${str}")
  }

  private def sha1(str: String): String = {
    val encoding = "UTF-8"
    val digest = MessageDigest.getInstance("SHA-1")

    digest.reset()
    digest.update(str.getBytes(encoding))
    digest.digest.map("%02x".format(_)).mkString
  }

}
