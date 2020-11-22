package hw.ppposd.lms.user

import hw.ppposd.lms.util.Id

case class User(id: Id[User], fullName: String, email: String, passwordHash: String)
