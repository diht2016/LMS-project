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
  - register with verificationCode (set email and password, password should be hard)
* User
  - view own User data and PersonalInfo
  - change password using old password (password should be hard)
  - edit own PersonalInfo (phone and links are validated)
  - view other User data and PersonalInfo (except learningBase)
* Student
  - view list of group courses
  - view list of group students
* Teacher
  - view list of leading courses

## Course actions
* User
  - view Course data (name, description)
  - view list of Teachers
  - view list of Tutors
  - view list of Materials
  - view list of started Homeworks
* Teacher
  - manage Tutors (add, delete)
  - manage Homeworks (add, edit, delete)
  - view full list of Homeworks
  - view sorted list of Solutions for Homework
  - view Solution text
* Student
  - set own Solution for Homework (if date fits limits)
* Tutor & Teacher
  - manage Materials (add, edit, delete)
