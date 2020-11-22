package hw.ppposd.lms.group

import hw.ppposd.lms.util.Id

case class Group(id: Id[Group],
                 name: String,
                 department: String,
                 courseNumber: Int)
