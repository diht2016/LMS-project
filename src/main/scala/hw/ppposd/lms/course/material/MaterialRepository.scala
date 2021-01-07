package hw.ppposd.lms.course.material

import java.sql.Timestamp
import java.time.LocalDateTime

import scala.concurrent.Future
import hw.ppposd.lms.Schema._
import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

trait MaterialRepository {
  def list(courseId: Id[Course]): Future[Seq[Material]]
  def add(courseId: Id[Course], name: String, description: String): Future[Id[Material]]
  def edit(materialId: Id[Material], name: String, description: String): Future[Int]
  def delete(materialId: Id[Material]): Future[Int]
  def find(id: Id[Material]): Future[Option[Material]]
}

class MaterialRepositoryImpl(implicit db: Database) extends MaterialRepository {
  override def list(courseId: Id[Course]): Future[Seq[Material]] =
    db.run(materials.filter(_.courseId === courseId).result)

  override def add(courseId: Id[Course], name: String, description: String): Future[Id[Material]] =
    db.run((materials returning materials.map(_.materialId))
      += Material(Id.auto, courseId, name, description, Timestamp.valueOf(LocalDateTime.now())))

  override def edit(materialId: Id[Material], name: String, description: String): Future[Int] =
    db.run(materials
      .filter(_.materialId === materialId)
      .map(m => (m.name, m.description))
      .update(name, description))

  override def delete(materialId: Id[Material]): Future[Int] =
    db.run(materials.filter(_.materialId === materialId).delete)

  override def find(id: Id[Material]): Future[Option[Material]] =
    db.run(materials.filter(_.materialId === id).result.headOption)
}
