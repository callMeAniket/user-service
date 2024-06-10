package controllers

import models.{User, UserLogin}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.UserService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def createUser() = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => {
        Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON format")))
      },
      user => {
        userService.createUser(user.name, user.email, user.password, user.phoneNumber, user.role).map { createdUser =>
          Created(Json.toJson(createdUser))
        }.recover {
          case e: Exception =>
            InternalServerError(Json.obj("message" -> "Failed to create user", "error" -> e.getMessage))
        }
      }
    )
  }

  def getUser(id: Int) = Action.async { implicit request =>
    userService.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found"))
    }.recover {
      case e: Exception =>
        InternalServerError(Json.obj("message" -> "Failed to retrieve user", "error" -> e.getMessage))
    }
  }

  def userLogin() = Action.async(parse.json) { implicit request =>
    request.body.validate[UserLogin].fold(
      _ => {
        Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON format")))
      },
      userLogin => {
        userService.getUserByEmail(userLogin.email).flatMap {
          case Some(user) if user.password == userLogin.password =>
            Future.successful(Ok(Json.toJson(user)))
          case Some(_) =>
            Future.successful(Unauthorized(Json.obj("message" -> "Invalid credentials")))
          case None =>
            Future.successful(NotFound(Json.obj("message" -> "User not found")))
        }.recover {
          case e: Exception =>
            InternalServerError(Json.obj("message" -> "Failed to login user", "error" -> e.getMessage))
        }
      }
    )
  }

  def updateUser(id: Int) = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => {
        Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON format")))
      },
      user => {
        userService.updateUser(id, user.name, user.email, user.password, user.phoneNumber, user.role).map {
          case 1 => Ok(Json.obj("message" -> "User updated successfully"))
          case 0 => NotFound(Json.obj("message" -> s"User with ID $id not found"))
        }.recover {
          case e: Exception =>
            InternalServerError(Json.obj("message" -> "Failed to update user", "error" -> e.getMessage))
        }
      }
    )
  }

  def deleteUser(id: Int) = Action.async { implicit request =>
    userService.deleteUser(id).map {
      case 1 => Ok(Json.obj("message" -> s"User deleted with ID: $id"))
      case 0 => NotFound(Json.obj("message" -> s"User with ID $id not found"))
    }.recover {
      case e: Exception =>
        InternalServerError(Json.obj("message" -> "Failed to delete user", "error" -> e.getMessage))
    }
  }

  def listUsers() = Action.async { implicit request =>
    userService.listUsers().map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case e: Exception =>
        InternalServerError(Json.obj("message" -> "Failed to retrieve users", "error" -> e.getMessage))
    }
  }
}
