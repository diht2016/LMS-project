package hw.ppposd.lms.course.material


import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.material.MaterialController.MaterialRequest
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future}

class MaterialController(materialRepo: MaterialRepository, accessRepo: AccessRepository)
                        (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User], courseId: Id[Course]): Route = {
    (pathEnd & get) {
      listMaterialsOfCourse(courseId)
    } ~ (pathEnd & post & patch & entity(as[MaterialRequest])) { m =>
        createNewMaterial(userId, courseId, m.name, m.description)
    } ~ (pathPrefixId[Material] & pathEnd & delete) { materialId =>
      deleteMaterial(userId, courseId, materialId)
    } ~ (pathPrefixId[Material] & pathEnd & put & patch & entity(as[MaterialRequest])) { (materialId, m) =>
        editMaterial(userId, courseId, materialId, m.name, m.description)
    }
  }

  def listMaterialsOfCourse(courseId: Id[Course]): Future[Seq[Material]] =
    materialRepo.list(courseId)

  def createNewMaterial(userId: Id[User], courseId: Id[Course], name: String, description: String): Future[Id[Material]] =
    for {
      canManage <- canManageMaterials(userId, courseId)
      newMaterialId <-
        if (canManage) {
          materialRepo.add(courseId, name, description)
        } else {
          ApiError(403, "User is not a teacher or a tutor.")
        }
    } yield newMaterialId

  def deleteMaterial(userId: Id[User], courseId: Id[Course], materialId: Id[Material]): Future[Seq[Material]] =
    for {
      canManage <- canManageMaterials(userId, courseId)
      rest <-
        if (canManage) {
          materialRepo.delete(courseId, materialId)
        } else {
          ApiError(403, "User is not a teacher or a tutor")
        }
    } yield rest

  def editMaterial(userId: Id[User], courseId: Id[Course], materialId: Id[Material], name: String, description: String): Future[Material] =
    for {
      canManage <- canManageMaterials(userId, courseId)
      editedMaterial <-
        if (canManage) {
          materialRepo.edit(materialId, name, description).flatMap {
            case Some(m) => Future.successful(m)
            case None => ApiError(404, s"Material with id=$materialId is not exists")
          }
        } else {
          ApiError(403, "User is not a teacher or a tutor")
        }
    } yield editedMaterial


  private def canManageMaterials(userId: Id[User], courseId: Id[Course]): Future[Boolean] = {
    val isTeacher = accessRepo.isCourseTeacher(userId, courseId)
    val isTutor = accessRepo.isCourseTeacher(userId, courseId)
    Future.find(List(isTeacher, isTutor)) { _ == true } map { _.isDefined }
  }
}

object MaterialController {
  case class MaterialRequest(name: String, description: String)

  object MaterialRequest extends PlayJsonSupport {
    implicit val materialRequestFormat: Format[MaterialRequest] = Json.format[MaterialRequest]
  }
}
