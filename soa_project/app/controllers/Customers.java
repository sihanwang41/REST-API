package controllers;
import play.*;
import play.mvc.*;
import play.libs.*;
import views.html.*;
import models.*;
import play.mvc.Http.*;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.BodyParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataServices.CustomersDataService;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Customers extends Controller {

	// Get a list of all customers
	public static Result get() {
		
		String query = null;
//		final Set<Map.Entry<String,String[]>> entries = null;
		
		String uri = request().uri();
		String path = request().path();
		
		if(uri.length() != path.length()){
			query = uri.substring(path.length()+6, uri.length()-3);
			System.out.println("query is "+ query);
//			entries = request().queryString().entrySet();
		}
	    
	    System.out.println("url is "+ uri);
	    System.out.println("path is "+ path);
	    
	    List<Customer> customers = CustomersDataService.get(query);
		
		return ok(Json.toJson(customers));
	}

	// Get a single customer
	public static Result getItem(int customer_id) {
		Customer customer = CustomersDataService.get(customer_id);
		return ok(Json.toJson(customer));
	}

	// Create a new customer
	// (I THINK Java Play framework should be able to figure out if a JSON object 
	// looks like a customer and automatically create the object and pass it in.)
	@BodyParser.Of(BodyParser.Json.class)
	public static Result create() {
		JsonNode json = request().body().asJson();
		Customer customer = new Customer(0, json.findPath("store_id").asInt(), json.findPath("first_name").textValue(), json.findPath("last_name").textValue(), json.findPath("email").textValue(), json.findPath("active").textValue(), json.findPath("address_id").asInt(), json.findPath("create_date").textValue(), json.findPath("last_update").textValue());
		CustomersDataService.create(customer);
		return ok(Json.toJson(customer));
	}
	// delete a customer
	public static Result deleteItem(int customer_id) {
		CustomersDataService.delete(customer_id);
		return ok();
	}
	
	// Update customer info
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateItem() {
		JsonNode json = request().body().asJson();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0000");
		//get current date time with Date()
	    Date date = new Date();
   	    //System.out.println(dateFormat.format(date));
	    
	    System.out.println(json);
	    
	    System.out.println(json.findPath("customer_id").asInt());
	    System.out.println(json.findPath("store_id").asInt());
	    System.out.println(json.findPath("first_name").textValue());
	    System.out.println(json.findPath("last_name").textValue());
	    System.out.println(json.findPath("email").textValue());
	    System.out.println(json.findPath("active").textValue());
	    System.out.println(json.findPath("address_id").asInt());
	    System.out.println(json.findPath("create_date").textValue());
	    
		Customer customer = new Customer(json.findPath("customer_id").asInt(), json.findPath("store_id").asInt(), json.findPath("first_name").textValue(), json.findPath("last_name").textValue(), json.findPath("email").textValue(), json.findPath("active").textValue(), json.findPath("address_id").asInt(), json.findPath("create_date").textValue(), date.toString());
		CustomersDataService.update(customer);
		
		return ok(Json.toJson(customer));
	}
	

}
