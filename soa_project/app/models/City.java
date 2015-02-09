package models;

public class City {
	
	public City(int city_id, String city, int country_id, String last_update){
		this.city_id = city_id;
		this.city = city;
		this.country_id = country_id;
		this.last_update = last_update;
	}
	
	public int city_id;
	public String city;
	public int country_id;
	public String last_update;
}
