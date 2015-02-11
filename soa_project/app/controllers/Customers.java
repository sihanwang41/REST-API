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
import java.io.UnsupportedEncodingException;
import java.lang.Object;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
	
	//private static String test = "/customers?q=\"lastname=Don&first_name=Edwards\"&limit=20&offset=0";
	// query_rule implies that attribute value could not be empty, e.g. "last_name="
	private static final String query_rule = ".*q='((.*=[\\w]+)+)'.*";
	private static final String limit_rule = ".*limit=([\\d]+)&offset=([\\d]+).*";
	private static final String field_rule = ".*field='(([\\w]+,?)+)'";
	// All the rules implies that if the parameter exists in the url, the value could not be empty
	
	private static Pattern query_pattern;
    private static Matcher query_matcher;
    private static Pattern limit_pattern;
    private static Matcher limit_matcher;
    private static Pattern field_pattern;
    private static Matcher field_matcher;
	
	// Get a list of all customers
	public static Result get() {
		
		String query = null;
		List<Customer> customers = null;
		int limit = 20;
		int offset = 0;
		final String url_head = "http://localhost:9000";
		String uri = request().uri();
		String path = request().path(); 
		String field = null;
		String tableName = null;
		
		try {
			uri = java.net.URLDecoder.decode(uri, "UTF-8");
			System.out.println(uri);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		query_pattern = Pattern.compile(query_rule);
		query_matcher = query_pattern.matcher(uri);
		
		limit_pattern = Pattern.compile(limit_rule);
		limit_matcher = limit_pattern.matcher(uri);
		
		field_pattern = Pattern.compile(field_rule);
		field_matcher = field_pattern.matcher(uri);
		
		if(query_matcher.find()){
			query = query_matcher.group(1);
			System.out.println("\nquery is "+ query+ "\n");
		}
		
		if(limit_matcher.find()){
			limit = Integer.parseInt(limit_matcher.group(1));
			offset = Integer.parseInt(limit_matcher.group(2));
			System.out.println("limit is " + limit + "     offset is " + offset + "\n");
		}
		
		if(field_matcher.find()){
			field = field_matcher.group(1);
			System.out.println("field is " + field + "\n");
		}
		
		tableName = path.substring(1);
		if(tableName.equals("customers")){
			tableName = "customer";
		}
		
		System.out.println("Table Name is " + tableName + "\n");
		
	    
	    customers = CustomersDataService.get(query, field, tableName);
	    
	    //convert result to json
		ResultNode result_node = new ResultNode();
		
		int pageContent = 0;
		if(customers.size() < limit){
			pageContent = customers.size();
		}
		else{
			pageContent = limit;
		}
		
		for (int i = offset; i < (offset + pageContent); i++) {
			CustomerNode element1 = new CustomerNode();
			link link1 = new link("self", url_head + path + "/" + customers.get(i).customer_id);
			link link2 = new link("streetAddress", url_head + "/address/" + customers.get(i).address_id);
			element1.setLink(link1);
			element1.setLink(link2);
			element1.setCustomer_id(customers.get(i).customer_id);
			element1.setStore_id(customers.get(i).store_id);
			element1.setName(customers.get(i).first_name, customers.get(i).last_name);	
			element1.setEmail(customers.get(i).email);
			element1.setAddress_id(customers.get(i).address_id);
			element1.setCreate_date(customers.get(i).create_date);
			element1.setLast_update(customers.get(i).last_update);
			result_node.add_customer(element1);
		}
		
		int length = customers.size();
		int pre_offset = offset - limit;
		int next_offset = offset + limit;
		String pre_url = null;
		String next_url = null;
		System.out.println(length);
		int las_offset = length-limit;
		if (las_offset < 0){
			las_offset = 0; // In case the total result set has less than the offset
		}
		
		if(pre_offset < 0){
			pre_offset = 0;
		}
		
		if(offset == 0){
			pre_url = "";
		}
		else{
			pre_url = url_head + path + "?limit=" + limit + "&offset=" + pre_offset;
		}
		
		if(next_offset >= length){
			next_url = "";
		}
		else{
			next_url = url_head + path + "?limit=" + limit + "&offset=" + next_offset;
		}
		
		String fir_url = url_head + path + "?limit=" + limit + "&offset=0";
		String las_url = url_head + path + "?limit=" + limit + "&offset=" + (las_offset);
		
		link first = new link("first", fir_url);
		link last = new link("last", las_url);
		link previous = new link("previous", pre_url);
		link next = new link("next", next_url);
		result_node.add_customer(first);
		result_node.add_customer(last);
		result_node.add_customer(previous);
		result_node.add_customer(next);
		
		return ok(new Gson().toJson(result_node));
	}

	// Get a single customer
	public static Result getItem(int customer_id) {
		// New part for projection and links
		//***************************************************************************
		final String url_head = "http://localhost:9000";
		String tableName = null;
		String field = null;
		
		String path = request().path();
		String uri = request().uri();
		
		// To extract the table name
		tableName = path.substring(path.indexOf("/")+1);
		tableName = tableName.substring(0, tableName.indexOf("/"));
				
		if(tableName.equals("customers")){
			tableName = "customer";
		}
		
		//System.out.println("Table Name is " + tableName);
		
		try {
			uri = java.net.URLDecoder.decode(uri, "UTF-8");
			//System.out.println(uri);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		field_pattern = Pattern.compile(field_rule);
		field_matcher = field_pattern.matcher(uri);
		
		if(field_matcher.find()){
			field = field_matcher.group(1);
			System.out.println("field is " + field + "\n");
		}
		
		Customer customer = CustomersDataService.getItem(customer_id, tableName, field);
		
		ResultNode result_node = new ResultNode();
		
		//System.out.println(path);
		
		CustomerNode element1 = new CustomerNode();
		link link1 = new link("self", url_head + path);
		
		link link2;
		if(customer.address_id == 0){
			link2 = new link("streetAddress", "");
		}
		else{
			link2 = new link("streetAddress", url_head + "/address/" + customer.address_id);
		}
		
		element1.setLink(link1);
		element1.setLink(link2);
		element1.setCustomer_id(customer.customer_id);
		element1.setStore_id(customer.store_id);
		element1.setName(customer.first_name, customer.last_name);	
		element1.setEmail(customer.email);
		element1.setAddress_id(customer.address_id);
		element1.setCreate_date(customer.create_date);
		element1.setLast_update(customer.last_update);
		result_node.add_customer(element1);
		//***************************************************************************
		
		
		return ok(new Gson().toJson(result_node));
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
	public static Result updateItem(int customer_id) {
		JsonNode json = request().body().asJson();
		
		System.out.println(request().body().asJson());
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0000");
		//get current date time with Dates()
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
