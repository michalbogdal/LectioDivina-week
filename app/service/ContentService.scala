package service

import akka.actor.ActorSystem
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import spray.json._
import DefaultJsonProtocol._
import spray.http._
import spray.client.pipelining._
import scala.concurrent.ExecutionContext.Implicits.global

object ContentService {

  case class DayPost(id: Int, title: String, content: String, date: String) {
    def formattedDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(this.date).toLocalDate
  }

  case class DayPostsResponse(status: String, count: Int, count_total: Int, pages: Int, posts: List[DayPost])

  implicit val postFormat = jsonFormat4(DayPost)
  implicit val postsFormat = jsonFormat5(DayPostsResponse)
  implicit val system = ActorSystem()

  def extract(responseFuture: Future[HttpResponse]): HttpResponse = Await.result(responseFuture, 20.seconds)


  def getDayPosts(from: org.joda.time.LocalDate): List[DayPost] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val numberOfDays = 6

    val posts = new ListBuffer[DayPost]()
    System.out.printf(s"Getting $numberOfDays posts from ${from}")
    for (f <- 0 to numberOfDays) yield {
      val day = from.plusDays(f)
      val response: Future[HttpResponse] = pipeline(Get(s"http://www.onjest.pl/slowo/?json=get_date_posts&date=$day&include=title,date,content"))
      val jsonBody:String = extract(response).entity.asString
      val postsResponse:DayPostsResponse = jsonBody.parseJson.convertTo[DayPostsResponse]
      if (!postsResponse.posts.isEmpty) {
        posts += postsResponse.posts.head
      }
    }

    return posts.toList
  }

}
