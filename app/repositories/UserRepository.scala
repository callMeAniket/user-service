package repositories

import javax.inject._
import models.User
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import play.api.Configuration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(config: Configuration)(implicit ec: ExecutionContext) {

  private val mongoUri: String = config.get[String]("mongo.uri")
  private val mongoClient: MongoClient = MongoClient(mongoUri)
  private val database: MongoDatabase = mongoClient.getDatabase(config.get[String]("mongo.database"))
  private val collection: MongoCollection[User] = database.getCollection(config.get[String]("mongo.collection"))

  def create(user: User): Future[Option[User]] = {
    collection.insertOne(user).toFuture()
    findById(user.id)
  }

  def findById(userId: String): Future[Option[User]] = {
    collection.find(equal("id", userId)).first().toFutureOption()
  }

  def update(userId: String, user: User): Future[Option[User]] = {
    collection.findOneAndUpdate(equal("id", userId), combine(set("name", user.name), set("email", user.email), set("password", user.password)))
      .toFutureOption()
  }
}

