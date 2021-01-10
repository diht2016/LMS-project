package hw.ppposd.lms.course.material

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.course.AccessRepository
import hw.ppposd.lms.course.material.MaterialController.MaterialEntity

import scala.concurrent.Future

class MaterialControllerSpec extends RouteSpecBase {
  import MaterialControllerSpec._

  "MaterialController" should "return list of course materials" in new TestWiring {
    materialRepoMock.list _ expects sampleCourseId returns
      Future.successful(sampleMaterials)

    Get() ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleListResponse
    }
  }

  it should "create new material if user is a course teacher" in new TestWiring {
    (materialRepoMock.add _).expects (sampleCourseId, sampleMaterial.name, sampleMaterial.description) returns
      Future.successful(sampleMaterialId)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    PostJson("/", sampleMaterialEntity) ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleMaterialId.toString
    }
  }

  it should "create new material if user is a course tutor" in new TestWiring {
    (materialRepoMock.add _).expects (sampleCourseId, sampleMaterial.name, sampleMaterial.description) returns
      Future.successful(sampleMaterialId)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    PostJson("/", sampleMaterialEntity) ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleMaterialId.toString
    }
  }

  it should "respond with 403 if other user is trying to create new material" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    PostJson("/", sampleMaterialEntity) ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorResponse
    }
  }

  it should "edit material if user is a course teacher" in new TestWiring {
    (materialRepoMock.edit _).expects (sampleMaterialId, sampleMaterial.name, sampleMaterial.description) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    PutJson(s"/$sampleMaterialId", sampleMaterialEntity) ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "edit material if user is a course tutor" in new TestWiring {
    (materialRepoMock.edit _).expects (sampleMaterialId, sampleMaterial.name, sampleMaterial.description) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    PutJson(s"/$sampleMaterialId", sampleMaterialEntity) ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 403 if other user is trying to edit material" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    PutJson(s"/$sampleMaterialId", sampleMaterialEntity) ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorResponse
    }
  }

  it should "delete material if user is a course teacher" in new TestWiring {
    (materialRepoMock.delete _).expects (sampleMaterialId) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    Delete(s"/$sampleMaterialId") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "delete material if user is a course tutor" in new TestWiring {
    (materialRepoMock.delete _).expects (sampleMaterialId) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    Delete(s"/$sampleMaterialId") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 403 if other user is trying to delete material" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseTutor _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    Delete(s"/$sampleMaterialId") ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorResponse
    }
  }

  trait TestWiring {
    val accessRepoMock: AccessRepository = mock[AccessRepository]
    val materialRepoMock: MaterialRepository = mock[MaterialRepository]
    val controller = new MaterialController(materialRepoMock, accessRepoMock)
    val route: Route = controller.route(sampleUserId, sampleCourseId)
  }
}

object MaterialControllerSpec {
  import hw.ppposd.lms.SampleDatabaseContent._

  private val sampleUserId = student1.id
  private val sampleCourseId = materialAlgebra1.courseId
  private val sampleMaterialId = materialAlgebra1.materialId
  private val sampleMaterial = materialAlgebra1
  private val sampleMaterialEntity =
    MaterialEntity(sampleMaterial.name, sampleMaterial.description)
  private val sampleMaterials = Seq(materialAlgebra1, materialAlgebra2)
  private val sampleListResponse = toJsonString(sampleMaterials)
  private val errorResponse = """{"error":"not permitted to manage materials"}"""
}
