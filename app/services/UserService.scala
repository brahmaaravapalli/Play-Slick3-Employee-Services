package services

import com.google.inject.Inject
import model.{Employee, Users}

import scala.concurrent.Future

class UserService @Inject() (users: Users) {

  def addUser(user: Employee): Future[String] = {
    users.add(user)
  }

  def deleteUser(id: Long): Future[Int] = {
    users.delete(id)
  }

  def getUser(id: Long): Future[Option[Employee]] = {
    users.get(id)
  }

  def listAllUsers: Future[Seq[Employee]] = {
    users.listAll
  }
}
