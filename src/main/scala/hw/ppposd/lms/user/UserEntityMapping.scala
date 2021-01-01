package hw.ppposd.lms.user

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.personaldata.{PersonalData, PersonalDataEntity}
import hw.ppposd.lms.user.studentdata.{StudentData, StudentDataEntity}
import hw.ppposd.lms.util.Id
import io.scalaland.chimney.dsl._

object UserEntityMapping {
  def modelToUserEntity(user: User,
                        group: Option[Group],
                        personalData: PersonalData,
                        studentDataOption: Option[StudentData],
                        showLearningBase: Boolean): UserEntity =
    UserEntity(
      user.fullName,
      user.email,
      personalData.into[PersonalDataEntity].transform,
      studentDataOption.map(_.into[StudentDataEntity]
        .withFieldComputed(_.group, _ => group.get)
        .withFieldComputed(_.learningBase, studentData => Option.when(showLearningBase)(studentData.learningBase))
        .transform)
    )

  def modelFromPersonalData(userId: Id[User], entity: PersonalDataEntity): PersonalData =
    entity.into[PersonalData]
      .withFieldComputed(_.userId, _ => userId)
      .transform
}
