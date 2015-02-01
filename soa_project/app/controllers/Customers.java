package controllers;
import play.*;
import play.mvc.*;
import play.libs.*;
import views.html.*;
import models.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.BodyParser;
import java.util.ArrayList;
import java.util.List;
import dataServices.CustomersDataService;

public class Customers extends Controller {

	// Get a list of all customers
	public static Result get() {
		List<Customer> customers = CustomersDataService.get();
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
	

}
