package controllers

import scala.concurrent.Future
import org.joda.time.DateTime
import play.api.data.JodaForms._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.concurrent.Execution.Implicits._

import play.api.i18n.{MessagesApi, I18nSupport}

import models.{Country,Currency, CountryModel}
import models.CountryModel._

import reactivemongo.api._
import reactivemongo.bson.{BSONObjectID,BSONDocument}


import javax.inject.Inject

class CountryController @Inject()(cc: ControllerComponents) extends AbstractController(cc)  with I18nSupport  {

    // Insert new country document
    def insert = Action.async(parse.json) { request => 
        request.body.validate[Country].map { country =>
            CountryModel.insert(country)
            Future(Ok(Json.obj("result" -> "success", "data" -> request.body)))
        }.getOrElse(Future.successful(BadRequest(Json.obj("result" -> "failed", "reason" -> "invalid json"))))
    }

    // Update country document
    def update = Action.async(parse.json) { request => 
        request.body.validate[Country].map { country =>
            val objectId = BSONObjectID.parse((request.body\ "_id").as[String])
            CountryModel.update(BSONDocument("_id" -> objectId.get),country)
            Future(Ok(Json.obj("result" -> "success", "data" -> request.body)))
        }.getOrElse(Future.successful(BadRequest(Json.obj("result" -> "failed", "reason" -> "invalid json"))))
    }

    //Delete country document
    def delete = Action.async(parse.json) { request => 
        request.body.validate[Country].map { country =>
            val objectId = BSONObjectID.parse((request.body\ "_id").as[String])
            CountryModel.removePermanently(BSONDocument("_id" -> objectId.get))
            Future(Ok(Json.obj("result" -> "success", "data" -> request.body)))
        }.getOrElse(Future.successful(BadRequest(Json.obj("result" -> "failed", "reason" -> "invalid json"))))
    }

    // Get All country document
    def getCountryList = Action.async(parse.json) { request => 
        CountryModel.find(BSONDocument()).map(data => {
            Ok(Json.obj("result" -> "success", "data" -> data))
        })
        
    }
}