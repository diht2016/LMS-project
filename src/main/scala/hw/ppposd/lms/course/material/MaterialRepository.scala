package hw.ppposd.lms.course.material

import java.sql.Timestamp
import java.time.LocalDateTime

import scala.concurrent.Future
import hw.ppposd.lms.Schema._
import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

trait MaterialRepository {

  /**
   * @return Materials related to the course with a given courseId
   */
  def list(courseId: Id[Course]): Future[Seq[Material]]

  /**
   * Adds new material to the course with a given courseId.
   * @return Id of a new material.
   */
  def add(courseId: Id[Course], name: String, description: String): Future[Id[Material]]

  /**
   * Updates name and description of a material with a given materialId if it exists
   * @return Updated material entry if it exists, None else.
   */
  def edit(materialId: Id[Material], name: String, description: String): Future[Option[Material]]

  /**
   * Deletes material with a given.
   * @return List of remained materials for the course with a given courseId.
   */
  def delete(courseId: Id[Course], materialId: Id[Material]): Future[Seq[Material]]

  /**
   * @return Material with a given id.
   */
  def find(id: Id[Material]): Future[Option[Material]]
}

class MaterialRepositoryImpl(implicit db: Database) extends MaterialRepository {
  override def list(courseId: Id[Course]): Future[Seq[Material]] =
    db.run(materials.filter(_.courseId === courseId).result)

  override def add(courseId: Id[Course], name: String, description: String): Future[Id[Material]] =
    db.run((materials returning materials.map(_.materialId))
      += Material(Id.auto, courseId, name, description, Timestamp.valueOf(LocalDateTime.now())))

  override def edit(materialId: Id[Material], name: String, description: String): Future[Option[Material]] = {
    val updateQuery = materials
      .filter(_.materialId === materialId)
      .map(m => (m.name, m.description))
      .update(name, description)
    db.run(updateQuery.andThen(materials.filter(_.materialId === materialId).result.headOption))
  }

  override def delete(courseId: Id[Course], materialId: Id[Material]): Future[Seq[Material]] = {
    val deleteQuery = materials.filter(_.materialId === materialId).delete
    db.run(deleteQuery.andThen(materials.filter(_.courseId === courseId).result))
  }

  override def find(id: Id[Material]): Future[Option[Material]] =
    db.run(materials.filter(_.materialId === id).result.headOption)
}
