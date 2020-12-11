# Project structure description

## Tables
- Group ({group.id}, name, department, courseNumber)
- Course ({course.id}, name, description)
- GroupCourse ({:group.id, course.id})
- Verification ({verificationCode}, fullName)
- User ({user.id}, fullName, email, password, userType)
- CourseTeacher ({:course.id, user#teacher.id})
- CourseTutor ({:course.id, user#student.id})
- Student ({user#student.id}, :group.id, yearOfEnrollment, degree, studyForm, learningBase)
- PersonalInfo ({user.id}, phoneNumber, city, description, vk, facebook, linkedin, instagram)
- Material ({material.id}, :course.id, name, description, creationDate)
- Homework ({homework.id}, :course.id, name, description, startDate, deadlineDate)
- Solution ({:homework.id, user#student.id}, text, date)

## Enums
- Degree [Bachelor, Specialist, Master]
- StudyForm [Intramural, Extramural, Evening]
- LearningBase [Contract, Budget]

## Platform actions
* Admin
  - create Group
  - create Course
  - create Verification (verificationCode is random)
  - add Group to Course
  - add Teacher to Course
* Guest
  - log in with email and password
    - `POST /auth/login (email, password)`
  - register with verificationCode (set email and password, password should be hard)
    - `POST /auth/register (verificationCode, email, password)`
* User
  - view own User data and PersonalInfo
    - `GET /users/me`
  - change password using old password (password should be hard)
    - `PUT /auth/change-password (oldPassword, newPassword)`
  - edit own PersonalInfo (phone and links are validated)
    - `PUT /users/me/personal-info (phoneNumber, city, description, vk, facebook, linkedin, instagram)`
  - view other User data and PersonalInfo (except learningBase)
    - `GET /users/{user.id}`
* Student
  - view list of group courses
    - `GET /courses`
  - view list of group students
    - `GET /classmates`
* Teacher
  - view list of leading courses
    - `GET /courses`

## Course actions
* User
  - view Course data (name, description)
    - `GET /courses/{course.id}`
  - view list of Teachers
    - `GET /courses/{course.id}/teachers`
  - view list of Tutors
    - `GET /courses/{course.id}/tutors`
  - view list of Materials
    - `GET /courses/{course.id}/materials`
  - view list of started Homeworks
    - `GET /courses/{course.id}/homeworks`
* Teacher
  - manage Tutors (add, delete)
    - `CREATE /courses/{course.id}/tutors (user.id)`
    - `DELETE /courses/{course.id}/tutors/{user.id}`
  - manage Homeworks (add, edit, delete)
    - `CREATE /courses/{course.id}/homeworks (name, description, startDate, deadlineDate)`
    - `PUT /courses/{course.id}/homeworks/{homeworks.id} (name, description, startDate, deadlineDate)`
    - `DELETE /courses/{course.id}/homeworks/{homeworks.id}`
  - view full list of Homeworks
    - `GET /courses/{course.id}/homeworks`
  - view sorted list of Solutions for Homework
    - `GET /courses/{course.id}/homeworks/{homeworks.id}/solutions`
  - view Solution text
    - `GET /courses/{course.id}/homeworks/{homeworks.id}/solutions/{solution.id}`
* Student
  - set own Solution for Homework (if date fits limits)
    - `POST /courses/{course.id}/homeworks/{homeworks.id}/solutions (text)`
* Tutor & Teacher
  - manage Materials (add, edit, delete)
    - `CREATE /courses/{course.id}/materials (name, description)`
    - `PUT /courses/{course.id}/materials/{materials.id} (name, description)`
    - `DELETE /courses/{course.id}/materials/{materials.id}`