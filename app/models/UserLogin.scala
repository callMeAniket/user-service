package models

case class UserLogin(email: String, password: String)

import play.api.libs.json._

object UserLogin {
  implicit val userFormat: OFormat[UserLogin] = Json.format[UserLogin]
}