package hw.ppposd.lms.course.material

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase

class MaterialRepositorySpec extends DatabaseSpecBase {

  "find" should "return a material with a given id" in new TestWiring {
    private val materialToFind = testData.materials(3)
    whenReady(repo.find(materialToFind.materialId)){
      _ shouldBe Some(materialToFind)
    }
  }

  "list" should "return all materials for the course" in new TestWiring {
    whenReady(repo.list(testData.courses(0).id)) {
      _ shouldBe testData.materials.slice(0, 2)
    }
  }

  "add" should "create a new material" in new TestWiring {
    private val before = Timestamp.valueOf(LocalDateTime.now.minusSeconds(1))
    private val newName = "new material name"
    private val newDescription = "new material"

    whenReady(repo.add(testData.courses(0).id, newName, newDescription)) { newId =>
      whenReady(repo.find(newId)) { newMaterialOption =>
        newMaterialOption.isDefined shouldBe true
        val newMaterial = newMaterialOption.get
        newMaterial.name shouldBe newName
        newMaterial.description shouldBe newDescription
        val now = Timestamp.valueOf(LocalDateTime.now)
        now.after(newMaterial.creationDate) shouldBe true
        before.before(newMaterial.creationDate) shouldBe true
      }
    }
  }

  "edit" should "update fields of existing material" in new TestWiring {
    private val newDescription = "new description"
    private val materialToEdit = testData.materials(1)
    private val materialEdited = materialToEdit.copy(description = newDescription)

    whenReady(repo.edit(materialToEdit.materialId, materialToEdit.name, newDescription)) { rowsChanged =>
      rowsChanged shouldBe 1
      whenReady(repo.find(materialToEdit.materialId)) {
        _ shouldBe Some(materialEdited)
      }
    }
  }

  "edit" should "return 0 if material does not exist" in new TestWiring {
    private val newDescription = "new description"
    private val materialToEdit = testData.materials(1)

    whenReady(repo.delete(materialToEdit.materialId)) { rowsChanged =>
      rowsChanged shouldBe 1
      whenReady(repo.edit(materialToEdit.materialId, materialToEdit.name, newDescription)) { rowsChanged =>
        rowsChanged shouldBe 0
      }
    }
  }

  "delete" should "delete the material with a given id" in new TestWiring {
    private val materialToDelete = testData.materials(4)
    whenReady(repo.delete(materialToDelete.materialId)) { rowsChanged =>
      rowsChanged shouldBe 1
      whenReady(repo.list(materialToDelete.courseId)) {
        _ shouldBe testData.materials.slice(2, 4)
      }
    }
  }

  trait TestWiring {
    val repo: MaterialRepository = new MaterialRepositoryImpl
  }
}
