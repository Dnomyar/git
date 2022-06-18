import java.security.MessageDigest

def hash(str: String): String = {
    val zeroByte = '\u0000'
    sha1(s"blob ${str.size}${zeroByte}${str}")
}

def sha1(str: String): String = {
  val encoding = "UTF-8"
  val digest = MessageDigest.getInstance("SHA-1")

  digest.reset
  digest.update(str.getBytes(encoding))
  digest.digest.map("%02x".format(_)).mkString
}
