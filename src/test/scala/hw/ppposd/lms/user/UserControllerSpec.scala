package hw.ppposd.lms.user

import akka.http.scaladsl.model.StatusCodes
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.group.GroupRepository
import hw.ppposd.lms.user.personaldata.{PersonalData, PersonalDataEntity}
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

class UserControllerSpec extends RouteSpecBase {
  import UserControllerSpec._

  "UserController" should "show authorized student" in new TestWiring {
    userRepoMock.find _ expects sampleStudentId returns
      Future.successful(Some(sampleStudent))
    userRepoMock.findPersonalData _ expects sampleStudentId returns
      Future.successful(Some(sampleStudentPersonalData))
    userRepoMock.findStudentData _ expects sampleStudentId returns
      Future.successful(Some(sampleStudentData))
    groupRepoMock.find _ expects sampleGroupId returns
      Future.successful(Some(sampleGroup))

    Get("/users/me") ~> controller.route(sampleStudentId) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleStudentResponseFull
    }
  }

  it should "show authorized teacher" in new TestWiring {
    userRepoMock.find _ expects sampleTeacherId returns
      Future.successful(Some(sampleTeacher))
    userRepoMock.findPersonalData _ expects sampleTeacherId returns
      Future.successful(Some(sampleTeacherPersonalData))
    (userRepoMock.findStudentData _ expects *).never()
    (groupRepoMock.find _ expects *).never()

    Get("/users/me") ~> controller.route(sampleTeacherId) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleTeacherResponse
    }
  }

  it should "show other student without LearningBase" in new TestWiring {
    userRepoMock.find _ expects sampleStudentId returns
      Future.successful(Some(sampleStudent))
    userRepoMock.findPersonalData _ expects sampleStudentId returns
      Future.successful(Some(sampleStudentPersonalData))
    userRepoMock.findStudentData _ expects sampleStudentId returns
      Future.successful(Some(sampleStudentData))
    groupRepoMock.find _ expects sampleGroupId returns
      Future.successful(Some(sampleGroup))

    Get(s"/users/${sampleStudentId.value}") ~> controller.route(sampleObserverId) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleStudentResponseHidden
      sampleStudentResponseHidden shouldBe sampleStudentResponseHiddenOtherWay
    }
  }

  it should "show other teacher" in new TestWiring {
    userRepoMock.find _ expects sampleTeacherId returns
      Future.successful(Some(sampleTeacher))
    userRepoMock.findPersonalData _ expects sampleTeacherId returns
      Future.successful(Some(sampleTeacherPersonalData))
    (userRepoMock.findStudentData _ expects *).never()
    (groupRepoMock.find _ expects *).never()

    Get(s"/users/${sampleTeacherId.value}") ~> controller.route(sampleObserverId) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleTeacherResponse
    }
  }

  it should "respond with 404 if no user found" in new TestWiring {
    userRepoMock.find _ expects sampleTeacherId returns
      Future.successful(None)

    private val sampleResponse =
      s"""{"error":"user not found"}"""

    Get(s"/users/${sampleTeacherId.value}") ~> controller.route(sampleObserverId) ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldBe sampleResponse
    }
  }

  it should "accept correct personal data" in new TestWiring {
    userRepoMock.setPersonalData _ expects samplePersonalData returns
      Future.successful(1)

    PutJson("/users/me/personal", samplePersonalDataEntity) ~>
      controller.route(sampleTeacherId) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "accept empty personal data" in new TestWiring {
    userRepoMock.setPersonalData _ expects samplePersonalDataEmpty returns
      Future.successful(1)

    PutJson("/users/me/personal", samplePersonalDataEntityEmpty) ~>
      controller.route(sampleTeacherId) ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "decline incorrect personal data and show which part is invalid" in new TestWiring {
    (userRepoMock.setPersonalData _ expects *).never()

    private def errorResponse(message: String) =
      s"""{"error":"$message"}"""

    PutJson("/users/me/personal", samplePersonalDataEntity.copy(phoneNumber = Some("123"))) ~>
      controller.route(sampleTeacherId) ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe errorResponse("invalid phone number")
    }

    PutJson("/users/me/personal", samplePersonalDataEntity.copy(vk = Some("https://example.com/test"))) ~>
      controller.route(sampleTeacherId) ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe errorResponse("invalid vk link")
    }
  }

  private trait TestWiring {
    protected val userRepoMock = mock[UserRepository]
    protected val groupRepoMock = mock[GroupRepository]
    protected val controller = new UserController(userRepoMock, groupRepoMock)
  }
}

object UserControllerSpec {
  import hw.ppposd.lms.SampleDatabaseContent._

  private val sampleObserverId = new Id[User](235)

  private val sampleGroupId = student1.groupId.get
  private val sampleGroup = group1
  private val sampleStudentId = student1.id
  private val sampleStudent = student1
  private val sampleStudentData = studentData1
  private val sampleStudentPersonalData = personalDataS1
  private val sampleStudentEntity = UserEntityMapping.modelToUserEntity(
    student1,
    Some(group1),
    personalDataS1,
    Some(studentData1),
    _
  )
  private val sampleStudentResponseFull = toJsonString(sampleStudentEntity(true))
  private val sampleStudentResponseHidden = toJsonString(sampleStudentEntity(false))
  private val sampleStudentResponseHiddenOtherWay =
    toJsonString(sampleStudentEntity(true).copy(studentData =
      sampleStudentEntity(true).studentData.map(_.copy(learningBase = None))))

  private val sampleTeacherId = teacher1.id
  private val sampleTeacher = teacher1
  private val sampleTeacherPersonalData = personalDataT1
  private val sampleTeacherEntity = UserEntityMapping.modelToUserEntity(
    teacher1,
    None,
    personalDataT1,
    None,
    showLearningBase = true
  )
  private val sampleTeacherResponse = toJsonString(sampleTeacherEntity)

  private val samplePersonalDataEntityEmpty =
    PersonalDataEntity(None, None, None, None, None, None, None)
  private val samplePersonalDataEntity = PersonalDataEntity(
    Some("+79001234567"),
    Some("Moscow"),
    Some("about"),
    Some("https://vk.com/my_nickname"),
    Some("https://facebook.com/some_nickname"),
    Some("https://linkedin.com/90432824123"),
    Some("https://instagram.com/my_cool_nickname"),
  )

  private val samplePersonalDataEmpty =
    PersonalData(sampleTeacherId, None, None, None, None, None, None, None)
  private val samplePersonalData = PersonalData(
    sampleTeacherId,
    Some("+79001234567"),
    Some("Moscow"),
    Some("about"),
    Some("https://vk.com/my_nickname"),
    Some("https://facebook.com/some_nickname"),
    Some("https://linkedin.com/90432824123"),
    Some("https://instagram.com/my_cool_nickname"),
  )
}
