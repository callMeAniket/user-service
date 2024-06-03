package models

import play.api.libs.json._

case class User(id: String, name: String, email: String, password: String)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}

