package example

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._

import java.io.{FileOutputStream, PrintStream}
import scala.io.Source


object Main extends App {

  case class CountrySource(
                            name: NameDetails,
                            capital: Seq[String],
                            area: Double,
                            region: String
                          )

  case class NameDetails(common: String)

  case class CountryRes(
                         name: String,
                         capital: String,
                         area: Double
                       )

  def source = Source.fromURL(
    "https://raw.githubusercontent.com/mledoze/countries/master/countries.json"
  )

  val data: String = source.getLines.mkString
  val result = decode[Seq[CountrySource]](data)
    .map(countries => countries
    .filter(_.region.equals("Africa"))
    .map(cs => CountryRes(cs.name.common, cs.capital.iterator.next(), cs.area))
    .sortWith(_.area > _.area)
    .take(10)
    .toList
  )

  val json = Encoder[List[CountryRes]].apply(result.getOrElse(Nil))

  val outputFile = args(0)
  val fos = new FileOutputStream(outputFile)
  val printer = new PrintStream(fos)
  printer.println(json)

}


