package controllers

import models.{User, UserLogin}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.UserService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def createUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => {
        Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON format")))
      },
      user => {
        userService.createUser(user).map { createdUser =>
          Created(Json.toJson(createdUser))
        }.recover {
          case e: Exception =>
            InternalServerError(Json.obj("message" -> "Failed to create user", "error" -> e.getMessage))
        }
      }
    )
  }

  def getUser(id: Int): Action[AnyContent] = Action.async { implicit request =>
    userService.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found"))
    }.recover {
      case e: Exception =>
        InternalServerError(Json.obj("message" -> "Failed to retrieve user", "error" -> e.getMessage))
    }
  }

  def getUserByToken: Action[AnyContent] = Action.async { implicit request =>
    print("Request reached in user service + " + request)
    userService.getUserByToken(request.headers.get("token").get).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found"))
    }.recover {
      case e: Exception =>
        InternalServerError(Json.obj("message" -> "Failed to retrieve user", "error" -> e.getMessage))
    }
  }

  def updateUser(id: Int): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => {
        Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON format")))
      },
      user => {
        userService.updateUser(id, user.name, user.email, user.password, user.phoneNumber, user.role, user.token).map {
          case 1 => Ok(Json.obj("message" -> "User updated successfully"))
          case 0 => NotFound(Json.obj("message" -> s"User with ID $id not found"))
        }.recover {
          case e: Exception =>
            InternalServerError(Json.obj("message" -> "Failed to update user", "error" -> e.getMessage))
        }
      }
    )
  }

  def deleteUser(id: Int): Action[AnyContent] = Action.async { implicit request =>
    userService.deleteUser(id).map {
      case 1 => Ok(Json.obj("message" -> s"User deleted with ID: $id"))
      case 0 => NotFound(Json.obj("message" -> s"User with ID $id not found"))
    }.recover {
      case e: Exception =>
        InternalServerError(Json.obj("message" -> "Failed to delete user", "error" -> e.getMessage))
    }
  }

  def listUsers(): Action[AnyContent] = Action.async { implicit request =>
    userService.listUsers().map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case e: Exception =>
        InternalServerError(Json.obj("message" -> "Failed to retrieve users", "error" -> e.getMessage))
    }
  }

  def userLogin(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserLogin].fold(
      _ => {
        Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON format")))
      },
      user => {
        userService.getUserByEmail(user.email).flatMap {
          case Some(fetchedUser) if user.password == fetchedUser.password =>
            userService.login(fetchedUser).map { s =>
              Ok(Json.obj("Token" -> s))
            }
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

    def userLogout(id: Int): Action[JsValue] = Action.async(parse.json) { implicit request =>
      userService.getUser(id).flatMap {
        case Some(user) if user.token == request.headers.get("token") =>
          userService.logout(user)
          Future.successful(Ok(Json.toJson(s"${user.name} logged out successfully.")))
        case Some(_) =>
          Future.successful(Unauthorized(Json.obj("message" -> "Invalid credentials")))
        case None =>
          Future.successful(NotFound(Json.obj("message" -> "User not found")))
      }.recover {
        case e: Exception =>
          InternalServerError(Json.obj("message" -> "Failed to logout user", "error" -> e.getMessage))
      }
    }
  }
