package models

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Success, Failure}

import org.joda.time.DateTime

import play.api.libs.concurrent._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.libs.concurrent.Execution.Implicits._

import reactivemongo.api._
import reactivemongo.bson._

case class Currency (
    name: String,
    code: String
)

case class Country (
    _creationDate: Option[DateTime],
    _updateDate: Option[DateTime],
    name: String,
    code: Option[String],
    currency: Option[Currency]
)

  
object CountryModel {

    implicit val CurrencyFormat = Json.format[Currency]    
    implicit val CountryFormat = Json.format[Country]
    
    // Use Reader to deserialize currency document automatically
	implicit object CurrencyBSONReader extends BSONDocumentReader[Currency] {
		def read(doc: BSONDocument): Currency = {
			Currency (
				doc.getAs[String]("name").get,
				doc.getAs[String]("code").get
			)
		}
	}
	
    // Use Reader to deserialize country document automatically	
	implicit object CountryBSONReader extends BSONDocumentReader[Country] {
		def read(doc: BSONDocument): Country = {
			Country(
				doc.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
				doc.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
				doc.getAs[String]("name").get,
				doc.getAs[BSONString]("code").map(c => c.value),
				doc.getAs[Currency]("currency").map(c => c)
			)
		}
	}
	
	// Use Writer to serialize currency document automatically
	implicit object CurrencyBSONWriter extends BSONDocumentWriter[Currency] {
		def write(currency: Currency): BSONDocument = {
			BSONDocument(
				"name" -> currency.name,
				"code" -> currency.code 
			)
		}
	}

	// Use Writer to serialize country document automatically	
	implicit object CountryBSONWriter extends BSONDocumentWriter[Country] {
		def write(country: Country): BSONDocument = {
			BSONDocument(
				"_creationDate" -> country._creationDate.map(date => BSONDateTime(date.getMillis)),
				"_updateDate" -> country._updateDate.map(date => BSONDateTime(date.getMillis)),
				"name" -> country.name,
				"code" -> country.code,
				"currency" -> country.currency 
			)
		}
	}
    
	// Call MongoDriver
	val driver = new MongoDriver
    
	// Connect to localhost
	val connection = driver.connection(List("localhost"))

	// Connect to mongodb
	val db = Await.result(connection.database("reactivemongo"), Duration(5000, "millis"))
	
	// Connect to mongodb collection
	val collection = db.collection("country")
	
	// Insert new country document using non blocking 
	def insert(c_doc:Country)= {
	  val future = collection.insert(c_doc.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime())))
	  
	  future.onComplete {
	  	case Failure(e) => throw e
	  	case Success(lastError) => {
	  		println("successfully inserted document object country = " + lastError)
	  	}
	  }
	}
	
	// Update country document using blocking
	def update(c_query:BSONDocument,c_modifier:Country) = {
	  collection.update(c_query, c_modifier.copy(_updateDate = Some(new DateTime())))
	}
	
	// Delete country document using blocking
	def removePermanently(p_query:BSONDocument) = {
	  collection.remove(p_query)
	}
	
	// Find all country documents using blocking
	def find(c_query:BSONDocument) = {
	  collection.find(c_query).cursor[Country]().collect[List]()
	}
	
	// Find one country document using blocking
	// Return the first found country document
	def findOne(c_query:BSONDocument) = {
	  collection.find(c_query).one[Country]
	}
	
	// Optional - Find all country document with filter
	def find(c_query:BSONDocument,c_filter:BSONDocument) = {}
	
	// Optional - Find all country document with filter and sorting
	def find(c_query:BSONDocument,c_filter:BSONDocument,c_sort:BSONDocument) = {}
	
}