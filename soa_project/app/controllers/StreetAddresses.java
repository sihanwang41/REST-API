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

import dataServices.AddressDataService;


import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.UnsupportedEncodingException;
import java.lang.Object;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class AddressNode {
    private int address_id;
    private contactAddress contact_address;
    private String phone;
    private String last_update;
    private List<link> links = new ArrayList<link>();
    
    public void setAddress_id (int i) {
    	this.address_id = i;
    }
    
    
    public void setPhone (String phone) {
    	this.phone = phone;
    }
    
    public void setLast_update (String s) {
    	this.last_update = s;
    }
    
    public void setLink(link l){	
    	this.links.add(l);
    }
    public void setContactAddress(String address, String address2, String district, int city_id, String postal_code) {
    	this.contact_address = new contactAddress();
    	contact_address.setAddress(address, address2, district, city_id, postal_code);
    }
}
class contactAddress {
    private String address;
    private String address2;
    private String district;
    private int city_id;
    private String postal_code;
    
    public void setAddress (String address, String address2, String district, int city_id, String postal_code) {
    	this.address = address;
    	this.address2 = address2;
    	this.district = district;
    	this.city_id = city_id;
    	this.postal_code = postal_code;
    }
}

class Address_resultNode {
	private ArrayList<AddressNode> street_addresses;
	private List<link> links = new ArrayList<link>();
	
	public Address_resultNode() {
		this.street_addresses = new ArrayList<AddressNode>();
		this.links = new ArrayList<link>();
	}
	
	public void add_address (AddressNode n) {
    	this.street_addresses.add(n);
    }
	
	public void add_address (link l) {
    	this.links.add(l);
    }
}
public class StreetAddresses extends Controller{
	// copy from class Customers
	private static final String query_rule = ".*q=\"((.*=[\\w]+)+)\".*";
	private static final String limit_rule = ".*limit=([\\d]+)&offset=([\\d]+).*";
	private static final String field_rule = ".*field=\"(([\\w]+,?)+)\"";
	// All the rules implies that if the parameter exists in the url, the value could not be empty
	
	private static Pattern query_pattern;
    private static Matcher query_matcher;
    private static Pattern limit_pattern;
    private static Matcher limit_matcher;
    private static Pattern field_pattern;
    private static Matcher field_matcher;
    public static Result get() {
		
		String query = null;
		List<Address> addr = null;
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
		if(tableName.equals("address")){
			tableName = "address";
		}
		
		System.out.println("Table Name is " + tableName + "\n");
		
	    
	    addr = AddressDataService.get(query, field, tableName);
	    
	    //convert result to json
	    Address_resultNode result_node = new Address_resultNode();
		
		int pageContent = 0;
		if(addr.size() < limit){
			pageContent = addr.size();
		}
		else{
			pageContent = limit;
		}
		
		for (int i = offset; i < (offset + pageContent); i++) {
			AddressNode element1 = new AddressNode();
			link link1 = new link("self", url_head + path + "/" + addr.get(i).address_id);
			link link2 = new link("City", url_head + "/city/" + addr.get(i).city_id);
			element1.setLink(link1);
			element1.setLink(link2);
			element1.setContactAddress(addr.get(i).address, addr.get(i).address2, addr.get(i).district, addr.get(i).city_id, addr.get(i).postal_code);
			element1.setAddress_id(addr.get(i).address_id);
			element1.setPhone(addr.get(i).phone);
			element1.setLast_update(addr.get(i).last_update);
			result_node.add_address(element1);
		}
		
		int length = addr.size();
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
		result_node.add_address(first);
		result_node.add_address(last);
		result_node.add_address(previous);
		result_node.add_address(next);
		
		return ok(new Gson().toJson(result_node));
	}
}
