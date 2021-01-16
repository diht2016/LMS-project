package hw.ppposd.lms.course.material

import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.material.MaterialController.MaterialEntity
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future}

class MaterialController(materialRepo: MaterialRepository, accessRepo: AccessRepository)
                        (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User], courseId: Id[Course]): Route = pathPrefix("materials") {
    pathEnd {
      get {
        listCourseMaterials(courseId)
      } ~ (post & entity(as[MaterialEntity])) { entity =>
        checkAccess(userId, courseId) accept createMaterial(courseId, entity)
      }
    } ~ (pathPrefixId[Material] & pathEnd) { materialId =>
      (put & entity(as[MaterialEntity])) { entity =>
        checkAccess(userId, courseId) accept editMaterial(materialId, entity)
      } ~ delete {
        checkAccess(userId, courseId) accept deleteMaterial(materialId)
      }
    }
  }

  private def listCourseMaterials(courseId: Id[Course]): Future[Seq[Material]] =
    materialRepo.list(courseId)

  private def createMaterial(courseId: Id[Course], entity: MaterialEntity): Future[Id[Material]] =
    materialRepo.add(courseId, entity.name, entity.description)

  private def editMaterial(materialId: Id[Material], entity: MaterialEntity): Future[Unit] =
    materialRepo.edit(materialId, entity.name, entity.description)
      .flatMap(assertSingleUpdate)

  private def deleteMaterial(materialId: Id[Material]): Future[Unit] =
    materialRepo.delete(materialId)
      .flatMap(assertSingleUpdate)

  private def canManageMaterials(userId: Id[User], courseId: Id[Course]): Future[Boolean] = {
    val isTeacher = accessRepo.isCourseTeacher(userId, courseId)
    val isTutor = accessRepo.isCourseTutor(userId, courseId)
    anyTrue(isTeacher, isTutor)
  }

  private def checkAccess[T](userId: Id[User], courseId: Id[Course]): Future[Unit] =
    assertTrue(canManageMaterials(userId, courseId),
      ApiError(403, "not permitted to manage materials"))
}

object MaterialController {
  case class MaterialEntity(name: String, description: String)

  object MaterialEntity extends PlayJsonSupport {
    implicit val materialEntityFormat: Format[MaterialEntity] = Json.format[MaterialEntity]
  }
}
