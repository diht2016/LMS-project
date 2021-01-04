package hw.ppposd.lms.user

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.group.GroupRepository
import hw.ppposd.lms.user.personaldata.PersonalDataEntity
import hw.ppposd.lms.user.personaldata.PersonalDataEntity.validateFields
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class UserController(userRepo: UserRepository, groupRepo: GroupRepository)
                    (implicit ec: ExecutionContext) extends Controller {
  import UserEntityMapping._
  def route(userId: Id[User]): Route = {
    pathPrefix("me") {
      (pathEnd & get) {
        getUserData(userId, isSelf = true)
      } ~ (path("personal") & patch & entity(as[PersonalDataEntity])) { entity =>
        setPersonalData(userId, entity)
      }
    } ~ (pathPrefixId[User] & pathEnd & get) { otherUserId =>
      getUserData(otherUserId, isSelf = false)
    }
  }

  private def getUserData(userId: Id[User], isSelf: Boolean): Future[UserEntity] = {
    userRepo.find(userId)
      .flatMap(assertFound("user"))
      .flatMap { user =>
      val groupOptionFuture = lookupIfSome(user.groupId, groupRepo.find)
      val personalDataFuture = userRepo.findPersonalData(userId).map(_.get)
      val studentDataOptionFuture = lookupIfNeeded(user.groupId, userRepo.findStudentData(userId))
      for {
        group <- groupOptionFuture
        personalData <- personalDataFuture
        studentData <- studentDataOptionFuture
      } yield modelToUserEntity(user, group, personalData, studentData, isSelf)
    }
  }

  private def setPersonalData(userId: Id[User], entity: PersonalDataEntity): Future[Unit] = {
    validateFields(entity) match {
      case None =>
        userRepo.setPersonalData(modelFromPersonalData(userId, entity))
        .flatMap(assertSingleUpdate)
      case Some(errorString) => ApiError(400, errorString)
    }
  }
}
