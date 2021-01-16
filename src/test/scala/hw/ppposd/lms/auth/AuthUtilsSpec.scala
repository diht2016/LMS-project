package hw.ppposd.lms.auth

import hw.ppposd.lms.base.SpecBase

class AuthUtilsSpec extends SpecBase {
  import AuthUtils._

  "hashPassword" should "return a SHA-256 hash" in {
    hashPassword("test_a") should not be hashPassword("test_b")
    hashPassword("test") should fullyMatch regex "[0-9a-f]{64}"
  }

  "isPasswordStrongEnough" should "return true on strong passwords" in {
    isPasswordStrongEnough("b53591c71d") shouldBe true
    isPasswordStrongEnough("return true on strong") shouldBe true
  }

  it should "return false on weak passwords" in {
    isPasswordStrongEnough("w53t") shouldBe false
    isPasswordStrongEnough("9chars ok") shouldBe false
    isPasswordStrongEnough("long_password") shouldBe false
    isPasswordStrongEnough("P@$$W0rd123456") shouldBe false
    isPasswordStrongEnough("qwerty12345p4ss$ssw0rd") shouldBe false
    isPasswordStrongEnough("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa") shouldBe false
  }

  "isEmailValid" should "return true on valid emails" in {
    isEmailValid("sample@example.com") shouldBe true
    isEmailValid("sample@long.domain.name.com") shouldBe true
    isEmailValid("test.Test_test@mail.ru") shouldBe true
    isEmailValid("aaa@t.com") shouldBe true
  }

  it should "return false on invalid emails" in {
    isEmailValid("test-email.ru") shouldBe false
    isEmailValid("@example.com") shouldBe false
    isEmailValid("example@.com") shouldBe false
    isEmailValid("example@com") shouldBe false
    isEmailValid("example@test.") shouldBe false
    isEmailValid("sample@example.com/test") shouldBe false
    isEmailValid("sample@test@example.com") shouldBe false
  }

  "randomSessionToken" should "return random string of 64 chars" in {
    randomSessionToken.length shouldBe 64
    randomSessionToken should not be randomSessionToken
  }

  "randomVerificationCode" should "return random string of 32 chars" in {
    randomVerificationCode.length shouldBe 32
    randomVerificationCode should not be randomVerificationCode
  }
}
