package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.User
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, userRepository: UserRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def register: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User].map { user =>
      userRepository.create(user).map { _ =>
        Created(Json.toJson(user))
      }
    }.getOrElse(Future.successful(BadRequest("Invalid data")))
  }

  def login: Action[JsValue] = Action.async(parse.json) { request =>
    // Implement login logic
    Future.successful(Ok)
  }

  def getProfile(userId: String): Action[AnyContent] = Action.async {
    userRepository.findById(userId).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def updateProfile(userId: String): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User].map { user =>
      userRepository.update(userId, user).map {
        case Some(updatedUser) => Ok(Json.toJson(updatedUser))
        case None => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid data")))
  }
}
