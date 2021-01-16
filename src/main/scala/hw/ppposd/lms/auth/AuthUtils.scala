package hw.ppposd.lms.auth

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

import com.typesafe.config.ConfigFactory

object AuthUtils {
  private val salt = ConfigFactory.load().getString("auth.password-salt")
  private val hashingInstance = MessageDigest.getInstance("SHA-256")
  private val randomInstance = new SecureRandom

  def hashPassword(password: String): String = {
    val saltedPassword = password + salt
    hashingInstance
      .digest(saltedPassword.getBytes("UTF-8"))
      .map("%02x".format(_)).mkString
  }

  def isPasswordStrongEnough(password: String): Boolean =
    password
      .replaceAll("(?i)(qwerty|asd|p[a4@][s$]+w[o0]rd|123(45?)?|(\\w)\\3{2,})", "_")
      .length >= 10

  def isEmailValid(email: String): Boolean =
    email.matches("^[\\w.-]+@[\\w-]+(\\.[\\w-]+)+$")

  def randomSessionToken: String = randomString(64)

  def randomVerificationCode: String = randomString(32)

  private def randomString(length: Int): String =
    new BigInteger(length * 5, randomInstance).toString(32)
}
