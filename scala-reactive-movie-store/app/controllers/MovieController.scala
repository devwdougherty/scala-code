package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import reactivemongo.api.bson.BSONObjectID
import repository.MovieRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class MovieController @Inject()(
                               implicit executionContext: ExecutionContext,
                               val movieRepository: MovieRepository,
                               val controllerComponents: ControllerComponents
                               ) extends BaseController {

  def findAll(): Action[AnyContent] = Action.async {

    implicit request: Request[AnyContent] => movieRepository.findAll().map {
      movies => Ok(Json.toJson(movies))
    }
  }

  def findOne(id: String): Action[AnyContent] = Action.async {

    implicit request: Request[AnyContent] => val objectIdTryResult = BSONObjectID.parse(id)
      objectIdTryResult match {
        case Success(objectId) => movieRepository.findOne(objectId).map {
          movie => Ok(Json.toJson(movie))
        }
        case Failure(_) => Future.successful(BadRequest("Cannot parse the movie id"))
      }
  }
}
