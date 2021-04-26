package controllers

import dto.NewTodoListItem
import models.TodoListItem
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

/*
 * Singleton - One instance of our controller to the application.
 * Inject() - PlayFramework -> pass the required class dependencies.
 */
@Singleton
class TodoListController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  private val todoList = new mutable.ListBuffer[TodoListItem]()

  todoList += TodoListItem(1, "test", true)
  todoList += TodoListItem(2, "some other value", false)

  /*
   * JSON Formatters
   */
  implicit val todoListJson = Json.format[TodoListItem];
  implicit val newTodoListItem = Json.format[NewTodoListItem];

  def addNewItem(): Action[AnyContent] = Action { implicit request =>

    // Reading the JSON Object and mapping it to an existent object.
    val content = request.body
    val jsonObject = content.asJson   // parses the given JSON object and returns an Option
    val todoListItem: Option[NewTodoListItem] =
      jsonObject.flatMap(
        Json.fromJson[NewTodoListItem](_).asOpt // asOpt makes the return be Option -> some or none.
      )

    // Adding the new item
    todoListItem match {
      case Some(newItem) =>
        val nextId = todoList.map(_.id).max + 1
        val toBeAdded = TodoListItem(nextId, newItem.description, false)
        todoList += toBeAdded
        Created(Json.toJson(toBeAdded))
      case None =>
        BadRequest
    }
  }

  /*
   * Action give us access to the request parameters and can return an HTTP response.
   */
  def getAll(): Action[AnyContent] = Action {

    if (todoList.isEmpty) {

      NoContent
    } else {

      Ok(Json.toJson(todoList))
    }
  }

  def getById(itemId: Long): Action[AnyContent] = Action {

    val foundItem = todoList.find(_.id == itemId)

    foundItem match {

      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def markAsDone(itemId: Long): Action[AnyContent] = Action {

    val foundItem = todoList.find(_.id == itemId)

    foundItem match {
      case Some(item) =>
        val newItem = item.copy(isItDone = true)
        todoList.dropWhileInPlace(_.id == itemId)
        todoList += newItem
        Accepted(Json.toJson(newItem))
      case None => NotFound
    }
  }

  def deleteAllDone: Action[AnyContent] = Action {

    todoList.filterInPlace(_.isItDone == false)

    Accepted
  }
}
