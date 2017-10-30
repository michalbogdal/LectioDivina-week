package controllers

import java.util

import service.ContentService
import play.api.mvc._
import play.api.libs.json._
import play.api.cache._
import javax.inject.Inject

import scala.concurrent.duration._

class Application  @Inject() (cache: CacheApi) extends Controller {
  implicit val DayPostsWrites = Json.writes[service.ContentService.DayPost]


  def index = Action {
    val from = org.joda.time.DateTime.now().plusDays(1).withDayOfWeek(1).toLocalDate
    val posts = getPosts(from)
    Ok (views.html.posts (posts) )
  }

  def weekPosts(date: String) = Action {
    var from = org.joda.time.DateTime.now().plusDays(1).withDayOfWeek(1).toLocalDate
    if(!date.isEmpty){
      from = new org.joda.time.LocalDate(date).withDayOfWeek(1)
    }

    val posts = getPosts(from)
    Ok (views.html.posts (posts) )
  }

  def getPosts(from: org.joda.time.LocalDate):List[service.ContentService.DayPost] = {
    val posts: List[ContentService.DayPost] = cache.getOrElse[List[service.ContentService.DayPost]] (s"posts-$from") {
      ContentService.getDayPosts(from);
    }
    cache.set (s"posts-$from", posts, 5.hours)
    return posts
  }
}
