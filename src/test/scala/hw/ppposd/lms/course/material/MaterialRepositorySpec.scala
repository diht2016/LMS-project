package hw.ppposd.lms.course.material

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class MaterialRepositorySpec extends DatabaseSpecBase{

  "find" should "return a material with a given id" in new TestWiring {
    val materialToFind = testData.materials(3)
    whenReady(repo.find(materialToFind.materialId), oneSecond){
      _ should be (Some(materialToFind))
    }
  }

  "list" should "return all materials for the course" in new TestWiring {
    val courseId = testData.courses(0).id
    whenReady(repo.list(courseId), oneSecond) {
      _.toList should be (testData.materials.slice(0, 2))
    }
  }

  "add" should "create a new material" in new TestWiring {
    val date = Timestamp.valueOf(LocalDateTime.now)
    private val newMaterial = Material(Id.auto, testData.courses(0).id, "name", "description", date)

    whenReady(repo.add(newMaterial.courseId, newMaterial.name, newMaterial.description), oneSecond) { newId =>
      val newMaterialWithId = newMaterial.copy(materialId = newId)
      whenReady(repo.find(newId), oneSecond) {
        _ should be (Some(newMaterialWithId))
      }
    }
  }

  "edit" should "update fields of material" in new TestWiring {
    val newDescription = "new description"
    val materialToEdit = testData.materials(1)
    val materialEdited = materialToEdit.copy(description = newDescription)

    whenReady(repo.edit(materialToEdit.materialId, materialToEdit.name, newDescription), oneSecond) {
      _ should be (Some(materialEdited))
    }
  }

  "delete" should "drop the material with a given id" in new TestWiring {
    val materialToDelete = testData.materials(4)
    whenReady(repo.delete(materialToDelete.courseId, materialToDelete.materialId), oneSecond) {
      _.toList should be (testData.materials.slice(2, 4))
    }
  }

  trait TestWiring {
    val repo: MaterialRepository = new MaterialRepositoryImpl
  }
}
