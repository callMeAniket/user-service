package models

import slick.jdbc.MySQLProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def password = column[String]("password")
  def phoneNumber = column[String]("phoneNumber")
  def role = column[String]("role")

  def * = (id.?, name, email, password, phoneNumber, role) <> ((User.apply _).tupled, User.unapply)
}