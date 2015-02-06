package controllers;
import play.*;
import play.mvc.*;
import play.libs.*;
import views.html.*;
import models.*;
import play.mvc.Http.*;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

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
import java.lang.Object;

class CustomerNode {
    private int customer_id;
    private int store_id;
    private Name name; 
    private String email;
    private int address_id;
    private int active;
    private String create_date;
    private String last_update;
    private List<link> links = new ArrayList<link>();
    
    public void setCustomer_id (int i) {
    	this.customer_id = i;
    }
    
    public void setStore_id (int i) {
    	this.store_id = i;
    }
    
    public void setName (String first_name, String last_name) {
    	this.name = new Name();
    	this.name.setName(first_name, last_name);
    }
    
    public void setEmail (String s) {
    	this.email = s;
    }
    
    public void setAddress_id (int i) {
    	this.address_id = i;
    }
    
    public void setCreate_date (String s) {
    	this.create_date = s;
    }
    
    public void setLast_update (String s) {
    	this.last_update = s;
    }
    
    public void setLink(link l){	
    	this.links.add(l);
    }
}

class Name {
    private String first_name;
    private String last_name;
    
    public void setName (String first_name, String last_name) {
    	this.first_name = first_name;
    	this.last_name = last_name;
    }
}

class link {
	private String rel;
	private String href;
	
	public link(String rel, String href){
		this.rel = rel;
		this.href = href;
	}
}

class ResultNode {
	private ArrayList<CustomerNode> customers;
	private List<link> links = new ArrayList<link>();
	
	public ResultNode() {
		this.customers = new ArrayList<CustomerNode>();
		this.links = new ArrayList<link>();
	}
	
	public void add_customer (CustomerNode n) {
    	this.customers.add(n);
    }
	
	public void add_customer (link l) {
    	this.links.add(l);
    }
}


public class Customers extends Controller {
	
	// Get a list of all customers
	public static Result get() {
		
		String query = null;
		String pagination = null;
		List<Customer> customers = null;
		int limit = 20;
		int offset = 0;
		int query_start = 0;
		int query_end = 0;
		String tmpquery = null;
		final String url_head = "http://localhost:9000";
		String uri = request().uri();
		String path = request().path(); 

		
		// condition 1 /customers
		if (uri.length() == path.length()) {
			customers = CustomersDataService.get(query, limit, offset);
			
			//convert result to json
			ResultNode result_node = new ResultNode();
			
			for (Customer element : customers) {
				CustomerNode element1 = new CustomerNode();
				link link1 = new link("self", url_head + path + "/" + element.customer_id);
				link link2 = new link("streetAddress", url_head + "/address/" + element.address_id);
				element1.setLink(link1);
				element1.setLink(link2);
				element1.setCustomer_id(element.customer_id);
				element1.setStore_id(element.store_id);
				element1.setName(element.first_name, element.last_name);	
				element1.setEmail(element.email);
				element1.setAddress_id(element.address_id);
				element1.setCreate_date(element.create_date);
				element1.setLast_update(element.last_update);
				result_node.add_customer(element1);
			}
			
			int length = customers.size();
			System.out.println(length);
			int las_offset = length-offset;
			if (las_offset < 0){
				las_offset = 0; // In case the total result set has less than the offset
			}
			String fir_url = url_head + path + "?limit=" + limit + "&offset=0";
			String las_url = url_head + path + "?limit=" + limit + "&offset=" + (las_offset);
			link first = new link("first", fir_url);
			link last = new link("last", las_url);
			result_node.add_customer(first);
			result_node.add_customer(last);
			
			return ok(new Gson().toJson(result_node));
			
			//return ok(Json.toJson(customers));
		}
		// condition 2 /customers?limit=x&offset=y
		if (uri.indexOf("q=") == -1) {
			query_start = uri.indexOf('?') + 1;
			query = uri.substring(query_start);
			String[] tokens = query.split("=|&");
			limit = Integer.parseInt(tokens[1]);
			offset = Integer.parseInt(tokens[3]);
			System.out.println(limit);
			System.out.println(offset);
			customers = CustomersDataService.get(null, limit, offset);
			return ok(Json.toJson(customers));
		}
		// condition 3 /customers?q="xxxxx"
		// condition 4 /customers?q="xxxxx"&limit=x&ofsset=y
		if(uri.length() != path.length()){
		
		query_start = uri.indexOf('%');
		//do not have query
		query = uri.substring(query_start+3);
		query_end = query.indexOf('%');
		int length = query.length();
		tmpquery = query.substring(0, query_end);
		
		if((query_end+3) < length){
			pagination = query.substring(query_end+4); // one more index for and
			String[] pag_arr = pagination.split("=|&");
			if (pag_arr[0].equals("limit")){
				limit = Integer.parseInt(pag_arr[1]);
			}
			
			if (pag_arr.length > 2){
				if (pag_arr[2].equals("offset")){
					offset = Integer.parseInt(pag_arr[3]);
				}
			}
		}
		
	}
		System.out.println("query is "+ query);
	    System.out.println("url is "+ uri);
	    System.out.println("path is "+ path);
	    
	    customers = CustomersDataService.get(tmpquery, limit, offset);
		
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
		
		System.out.println(request().body().asJson());
		
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
