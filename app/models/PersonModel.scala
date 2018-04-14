package models

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Success, Failure}

import org.joda.time.DateTime

import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits._

import reactivemongo.api._
import reactivemongo.bson._

case class Person (
    _id: BSONObjectID,
    _creationDate: Option[DateTime],
    _updateDate: Option[DateTime],
    name: String,
    dob: Option[DateTime],
    age: Option[Int],
    salary: Double,
    admin: Boolean,
    hobbies: List[String],
    address: Address
)

case class Address (
    address: String,
    country: String
)
  
object PersonModel {
  
    // Use Reader to deserialize address document automatically
	implicit object AddressBSONReader extends BSONDocumentReader[Address] {
		def read(doc: BSONDocument): Address = {
			Address (
				doc.getAs[String]("address").get,
				doc.getAs[String]("country").get
			)
		}
	}
	
	// Use Reader to deserialize persion document automatically
	implicit object PersonBSONReader extends BSONDocumentReader[Person] {
		def read(doc: BSONDocument): Person = {
			Person(
				doc.getAs[BSONObjectID]("_id").get,
				doc.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
				doc.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
				doc.getAs[String]("name").get,
				doc.getAs[BSONDateTime]("dob").map(dt => new DateTime(dt.value)),
				doc.getAs[BSONInteger]("age").map(v => if(v.value.isValidInt) v.value else 0),
				doc.getAs[Double]("salary").getOrElse(0.0),
				doc.getAs[Boolean]("admin").getOrElse(false),
				doc.getAs[List[String]]("hobbies").getOrElse(List()),
				doc.getAs[Address]("address").getOrElse(null)
			)
		}
	}
	
	// Use Writer to serialize address document automatically
	implicit object AddressBSONWriter extends BSONDocumentWriter[Address] {
		def write(person: Address): BSONDocument = {
			BSONDocument(
				"address" -> person.address,
				"country" -> person.country 
			)
		}
	}

	// Use Writer to serialize person document automatically	
	implicit object PersonBSONWriter extends BSONDocumentWriter[Person] {
		def write(person: Person): BSONDocument = {
			BSONDocument(
				"_id" -> person._id,
				"_creationDate" -> person._creationDate.map(date => BSONDateTime(date.getMillis)),
				"_updateDate" -> person._updateDate.map(date => BSONDateTime(date.getMillis)),
				"name" -> person.name,
				"dob" -> person.dob.map(date => BSONDateTime(date.getMillis)),
				"age" -> person.age,
				"salary" -> person.salary,
				"admin" -> person.admin,
				"hobbies" -> person.hobbies,
				"address" -> person.address 
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
	val collection = db.collection("persons")
	
	// Insert new person document using non blocking 
	def insert(p_doc:Person)= {
	  val future = collection.insert(p_doc.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime())))
	  
	  future.onComplete {
	  	case Failure(e) => throw e
	  	case Success(lastError) => {
	  		println("successfully inserted document with lastError = " + lastError)
	  	}
	  }
	}
	
	// Update person document using blocking
	def update(p_query:BSONDocument,p_modifier:Person) = {
	  collection.update(p_query, p_modifier.copy(_updateDate = Some(new DateTime())))
	}
	
	// Optional - Soft deletion by setting deletion flag in document
	def remove(p_query:BSONDocument) = {}
	
	// Delete person document using blocking
	def removePermanently(p_query:BSONDocument) = {
	  collection.remove(p_query)
	}
	
	// Find all person documents using blocking
	def find(p_query:BSONDocument) = {
	  collection.find(p_query).cursor[Person]().collect[List]()
	}
	
	// Find one person document using blocking
	// Return the first found document
	def findOne(p_query:BSONDocument) = {
	  collection.find(p_query).one[Person]
	}
	
	// Optional - Find all person document with filter
	def find(p_query:BSONDocument,p_filter:BSONDocument) = {}
	
	// Optional - Find all person document with filter and sorting
	def find(p_query:BSONDocument,p_filter:BSONDocument,p_sort:BSONDocument) = {}
	
}