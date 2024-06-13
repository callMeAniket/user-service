package services

import daos.UserDAO
import models.User
import scala.util.Random
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(userDAO: UserDAO)(implicit ec: ExecutionContext) {
  private val ADMIN: String = "Admin"

  def createUser(user: User): Future[User] = {
    if (user.role.equalsIgnoreCase(ADMIN)) {
      checkForAdminUser().flatMap {
        case Some(_) => Future.failed(new Exception("Failed to create admin user, Admin User already exists"))
        case None => handleCreate(user)
      }
    } else {
      handleCreate(user)
    }
  }

  private def handleCreate(user: User): Future[User] = {
    userDAO.create(user).recoverWith {
      case e: Exception =>
        Future.failed(new Exception("Failed to create user", e))
    }
  }

  private def checkForAdminUser(): Future[Option[User]] = {
    userDAO.fetchAdminUser()
  }

  def updateUser(id: Int, name: String, email: String, password: String, phoneNumber: String, role: String, token: Option[String]): Future[Int] = {
    val updatedUser = User(Some(id), name, email, password, phoneNumber, role, token)
    userDAO.update(updatedUser).recoverWith {
      case e: Exception =>
        Future.failed(new Exception(s"Failed to update user with ID $id", e))
    }
  }

  def deleteUser(id: Int): Future[Int] = {
    getUser(id).flatMap {
      case Some(user) =>
        if(user.role.equalsIgnoreCase(ADMIN)) {
         return Future.failed(new Exception("Admin User can not be deleted."))
        }
        userDAO.delete(id).recoverWith {
          case e: Exception =>
            Future.failed(new Exception(s"Failed to delete user with ID $id", e))
        }
      case _ => Future.failed(new Exception("User does not exist"))
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

  def getUserByToken(token: String): Future[Option[User]] = {
    userDAO.findByToken(token).recoverWith {
      case e: Exception =>
        Future.failed(new Exception("Failed to find user "))
    }
  }

  def listUsers(): Future[Seq[User]] = {
    userDAO.findAll().recoverWith {
      case e: Exception =>
        Future.failed(new Exception("Failed to retrieve users", e))
    }
  }

  def login(user: User): Future[String] = {
    userDAO.login(user, generateRandomString())
  }

  def logout(user: User): Unit = {
    userDAO.logout(user)
  }

  private def generateRandomString(): String = {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_=+[]{}|;:,.<>?/`~"
    val sb = new StringBuilder
    for (_ <- 1 to 50) {
      val randomChar = chars.charAt(Random.nextInt(chars.length))
      sb.append(randomChar)
    }
    sb.toString()
  }
}
