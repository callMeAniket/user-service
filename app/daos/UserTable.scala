package daos

import models.User
import slick.jdbc.MySQLProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def password = column[String]("password")
  def phoneNumber = column[String]("phoneNumber")
  def role = column[String]("role")
  def token = column[Option[String]]("token")

  def * = (id.?, name, email, password, phoneNumber, role, token) <> ((User.apply _).tupled, User.unapply)
}