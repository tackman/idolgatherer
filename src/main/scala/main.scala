import dispatch._
import Defaults._
import net.liftweb.json._
import sys.process._

/**
 * Created by takuma on 2014/02/04.
 */

object main {
  val saveDir = "/Users/takuma/Pictures/imas_cg/"

  def main(args: Array[String]) = {
    println("Hi!")

    // モバマスDB
    // http://www5164u.sakura.ne.jp/
    // 全アイドルプロフィール一覧
    val idolsRes = Http(url("http://www5164u.sakura.ne.jp/idols/profile.json?scope=all") OK as.String)
    val idolsStr = idolsRes()
    println(idolsStr)

    val json = parseOpt(idolsStr) match {
      case Some(x) => x.asInstanceOf[JArray]
      case None => sys.exit(1)
    }

    val list = json.children
    list.foreach(j => {
      val name = j.\\("name").asInstanceOf[JString]
      val id = j.\\("id").asInstanceOf[JInt]
      val imgUrl = getImgUrl(name.values)
      println(id.values + name.values + imgUrl)

      "wget " + imgUrl + " -O " + saveDir + id.values + ".jpg" !!

      Thread.sleep(1000)
    })

  }

  def getImgUrl(name: String): String = {
    val encoded = java.net.URLEncoder.encode(name, "UTF-8");
    val query = "https://www.google.co.jp/search?q=" + encoded + "&tbm=isch"
    val res = Http(url(query) OK as.String)
    val resStr = res()
    println(resStr)

    val imgUrlPattern = "<img height=\"[0-9]+\" src=\"([^\"]+)\"".r
    val matcher = imgUrlPattern.findFirstMatchIn(resStr).get

    return matcher.group(1)
  }

}
