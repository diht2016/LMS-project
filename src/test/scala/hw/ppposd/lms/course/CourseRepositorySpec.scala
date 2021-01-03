package hw.ppposd.lms.course

import hw.ppposd.lms.SpecBaseDb
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import hw.ppposd.lms.SpecSchema._


class CourseRepositorySpec extends SpecBaseDb {

  trait testing {
    val repository = new CourseRepositoryImpl()
  }

  "list" should "return all courses" in new testing {
    repository.list().futureValue should be (coursesData)
  }
}

