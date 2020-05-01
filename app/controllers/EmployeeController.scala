package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.Logging
import play.api.libs.concurrent.Futures
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request, Result}
import services.UserService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import model.Employee


@Singleton
class EmployeeController @Inject()(cc: ControllerComponents, userService: UserService) (implicit futures: Futures) extends AbstractController(cc) with Logging {

  def index = Action {
    Ok(views.html.index())
  }

  def employees() = Action.async { implicit request: Request[AnyContent] =>
    userService.listAllUsers.map(user => Ok(Json.toJson(user)))
    }

  def CreateUserId = Action { request =>
    val json = request.body.asJson.get
    val employee = json.as[Employee]
    userService.addUser(employee).map(user => Ok(Json.toJson(user)))
     println(employee)
    Ok
  }

/*
  def updateEmployee() = Action { implicit request: Request[AnyContent] =>
    val jsonBody = request.body.asJson.get
    val newEmp = jsonBody.as[Employee]

    Employee.update(newEmp.id, newEmp.name, newEmp.email)

    val emp = Employee.findById(newEmp.id)
    emp match {
      case Some(x) => Ok(Json.toJson(emp)).as("application/json")
      case None => Ok("Employee NOT Found in database")
    }
  }
*/

  def deleteEmployee(id: Long) = Action { implicit request: Request[AnyContent] =>
    userService.deleteUser(id)
    NoContent
  }


}
