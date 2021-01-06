package hw.ppposd.lms.course.material

import java.sql.Timestamp

import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.course.{AccessRepository, Course, CourseWiring}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

class MaterialControllerSpec extends RouteSpecBase {
  import MaterialControllerSpec._

  "MaterialController" should "return list of course materials" in new TestWiring {

  }

  it should "create new material if user is a teacher/tutor" in new TestWiring {

  }

  it should "throw error 403 if not teacher/tutor is trying to create new material" in new TestWiring {

  }

  it should "delete  material if user is a teacher/tutor" in new TestWiring {

  }

  it should "throw error 403 if not teacher/tutor is trying to delete material" in new TestWiring {

  }

  it should "edit material if user is a teacher/tutor" in new TestWiring {

  }

  it should "throw error 403 if not teacher/tutor is trying to edit material" in new TestWiring {

  }

  trait TestWiring {
    val accessRepoMock: AccessRepository = mock[AccessRepository]
    val materialRepoMock: MaterialRepository = mock[MaterialRepository]
    val wiringMock: CourseWiring = mock[CourseWiring]
    val controller = new MaterialController(materialRepoMock, accessRepoMock)
  }
}

object MaterialControllerSpec {
  val sampleUserId = new Id[User](234)
  val sampleCourseId = new Id[Course](11)
  val sampleMaterialId = new Id[Material](71)
  val creationDate = Timestamp.valueOf("2020-12-25 12:30:00")

  val sampleMaterial = Material(sampleMaterialId, sampleCourseId, "name", "description", creationDate)
}
