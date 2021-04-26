package model

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
/* Optimizing the imports leads a build error. */

case class Movie (
                   _id: Option[BSONObjectID],
                   _creationDate: Option[DateTime],
                   _updateDate: Option[DateTime],
                   title: String,
                   description: String
                 )
object Movie {

  /* Creating the JSON Format for the model */
  implicit val movieFormat: Format[Movie] = Json.format[Movie]

  /* BSON Reader to Object */
  implicit object MovieBSONReader extends BSONDocumentReader[Movie] {

    def read(docBson: BSONDocument): Movie = {
      Movie(
        docBson.getAs[BSONObjectID]("_id"),
        docBson.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
        docBson.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
        docBson.getAs[String]("title").get,
        docBson.getAs[String]("description").get
      )
    }
  }

  /* Object to BSON Writer */
  implicit object MovieBSONWriter extends BSONDocumentWriter[Movie] {

    override def write(movie: Movie): BSONDocument = {
      BSONDocument(
        "_id" -> movie._id,
        "_creationDate" -> movie._creationDate.map(dt => BSONDateTime(dt.getMillis)),
        "_updateDate" -> movie._updateDate.map(dt => BSONDateTime(dt.getMillis)),
        "title" -> movie.title,
        "description" -> movie.description
      )
    }
  }
}
