package daos

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.MySQLProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[MySQLProfile]
  import dbConfig._
  import profile.api._

  val users = TableQuery[UserTable]

  def create(user: User): Future[User] = {
    val insertQuery = (users returning users.map(_.id)
      into ((user, id) => user.copy(id = Some(id)))
      ) += user
    db.run(insertQuery)
  }

  def findById(id: Int): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def findByEmailId(emailId: String): Future[Option[User]] = {
    db.run(users.filter(_.email === emailId).result.headOption)
  }

  def findByToken(token: String): Future[Option[User]] = {
    db.run(users.filter(_.token === token).result.headOption)
  }

  def fetchAdminUser(): Future[Option[User]] = {
    db.run(users.filter(_.role.toLowerCase === "admin").result.headOption)
  }

  def update(user: User): Future[Int] = {
    db.run(users.filter(_.id === user.id).update(user))
  }

  def delete(id: Int): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }

  def findAll(): Future[Seq[User]] = {
    db.run(users.result)
  }

  def login(user: User, useThisToken: String): Future[String] = {
    val update: User = user.copy(token = Some(useThisToken))
    db.run(users.filter(_.id === user.id).update(update))
    Future.successful(useThisToken)
  }

  def logout(user: User): Unit = {
    val update: User = user.copy(token = None)
    db.run(users.filter(_.id === user.id).update(update))
  }
}