package services

import daos.UserDAO
import models.User

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(userDAO: UserDAO)(implicit ec: ExecutionContext) {

  def createUser(name: String, email: String, password: String, phoneNumber: String, role: String): Future[User] = {
    val user = User(None, name, email, password, phoneNumber, role )
    userDAO.create(user).recoverWith {
      case e: Exception =>
        Future.failed(new Exception("Failed to create user", e))
    }
  }

  def updateUser(id: Int, name: String, email: String, password: String, phoneNumber: String, role: String): Future[Int] = {
    val updatedUser = User(Some(id), name, email, password, phoneNumber, role)
    userDAO.update(updatedUser).recoverWith {
      case e: Exception =>
        Future.failed(new Exception(s"Failed to update user with ID $id", e))
    }
  }

  def deleteUser(id: Int): Future[Int] = {
    userDAO.delete(id).recoverWith {
      case e: Exception =>
        Future.failed(new Exception(s"Failed to delete user with ID $id", e))
    }
  }

  def getUser(id: Int): Future[Option[User]] = {
    userDAO.findById(id).recoverWith {
      case e: Exception =>
        Future.failed(new Exception(s"Failed to find user with ID $id", e))
    }
  }

  def getUserByEmail(emailId: String): Future[Option[User]] = {
    userDAO.findByEmailId(emailId).recoverWith {
      case e: Exception =>
        Future.failed(new Exception(s"Failed to find user with email $emailId", e))
    }
  }

  def listUsers(): Future[Seq[User]] = {
    userDAO.findAll().recoverWith {
      case e: Exception =>
        Future.failed(new Exception("Failed to retrieve users", e))
    }
  }
}
