package hw.ppposd.lms

import hw.ppposd.lms.auth.Verification
import hw.ppposd.lms.course.Course
import hw.ppposd.lms.course.homework.Homework
import hw.ppposd.lms.course.homework.solution.Solution
import hw.ppposd.lms.course.material.Material
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.user.personaldata.PersonalData
import hw.ppposd.lms.user.studentdata.StudentData

case class TestData(users: Seq[User] = Seq(),
                    courses: Seq[Course] = Seq(),
                    groups: Seq[Group] = Seq(),
                    studentData: Seq[StudentData] = Seq(),
                    personalData: Seq[PersonalData] = Seq(),
                    homeworks: Seq[Homework] = Seq(),
                    solutions: Seq[Solution] = Seq(),
                    materials: Seq[Material] = Seq(),
                    verifications: Seq[Verification] = Seq(),
                   )
