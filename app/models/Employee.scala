package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Format, JsNumber, JsObject, JsResult, JsString, JsSuccess, JsValue}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class Employee(id: Long, firstName: String, lastName: String, mobile: Long, email: String)
final case class Failed(message: String, code: Int)
object Employee extends ((Long , String, String, Long, String) => Employee) {

  implicit object EmployeeFormat extends Format[Employee] {
    def writes(emp: Employee): JsValue = {
      val empSeq = Seq(
        "id" -> JsNumber(emp.id),
        "firstName" -> JsString(emp.firstName),
        "lastName" -> JsString(emp.lastName),
        "mobile" -> JsNumber(emp.mobile),
        "email" -> JsString(emp.email)
      )
      JsObject(empSeq)
    }

    def reads(json: JsValue): JsResult[Employee] = {
      val id = (json \ "id").as[Long]
      val firstName = (json \ "firstName").as[String]
      val lastName = (json \ "lastName").as[String]
      val mobile = (json \ "mobile").as[Long]
      val email = (json \ "email").as[String]
      JsSuccess(Employee(id, firstName, lastName, mobile, email))
    }

  }

}
import slick.jdbc.MySQLProfile.api._


  class UserTableDef(tag: Tag) extends Table[Employee](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def mobile = column[Long]("mobile")
    def email = column[String]("email")

    override def * =
      (id, firstName, lastName, mobile, email) <> (Employee.tupled, Employee.unapply)
  }


  class Users @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

    val users = TableQuery[UserTableDef]

    def add(user: Employee): Future[String] = {
      dbConfig.db.run(users += user).map(res => "User successfully added").recover {
        case ex: Exception => ex.getCause.getMessage
      }
    }

    def delete(id: Long): Future[Int] = {
      dbConfig.db.run(users.filter(_.id === id).delete)
    }

    def get(id: Long): Future[Option[Employee]] = {
      dbConfig.db.run(users.filter(_.id === id).result.headOption)
    }


    def listAll: Future[Seq[Employee]] = {
      dbConfig.db.run(users.result)
    }

}