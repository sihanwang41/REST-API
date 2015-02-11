package dataServices;
import java.util.List;

import models.City;
import models.City;
import models.Country;
public class CityDataService {
	public static List<City> get(String query, String field, String tableName){
		List<City> list = null;
		String sql = "select ";
		String[] tokens = null;
		
		if (field == null){
			sql += "* ";
		}
		else{
			tokens = field.split(",");
			for (String token : tokens){
				sql = sql + token + ", ";
			}
			sql = sql.substring(0, sql.length()-2); // To get rid of the extra comma
			sql += " ";
		}
		
		sql = sql + "from " + tableName + " ";
		
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			if(query == null){
				
				list = conn.createQuery(sql)
							.executeAndFetch(City.class);
			}
			else{
				
				sql += "where ";
				while(query != null){
					int equal_pos = query.indexOf('=');
					sql = sql + query.substring(0, equal_pos) + " = ";
					int and_op = query.indexOf('&');
					int or_op = query.indexOf("||");
					System.out.println("and");
					if (and_op >= 0 && or_op == -1) {
						if (isNumeric(query.substring(equal_pos+1, and_op))){
							sql = sql + query.substring(equal_pos+1, and_op) + " and ";
						}
						else{
							sql = sql + "'" + query.substring(equal_pos+1, and_op) + "'" + " and ";
						}
						query = query.substring(and_op+1);
					}
					else if (or_op >= 0 && and_op == -1)  {
						if (isNumeric(query.substring(equal_pos+1, or_op))){
							sql = sql + query.substring(equal_pos+1, or_op) + " or ";
						}
						else{
							sql = sql + "'" + query.substring(equal_pos+1, or_op) + "'" + " or ";
						}
						query = query.substring(or_op+2);
					}
					else if (or_op >= 0 && and_op >= 0) {
						if ((and_op < or_op)){
							if (isNumeric(query.substring(equal_pos+1, and_op))){
								sql = sql + query.substring(equal_pos+1, and_op) + " and ";
							}
							else{
								sql = sql + "'" + query.substring(equal_pos+1, and_op) + "'" + " and ";
							}
							query = query.substring(and_op+1);
//							System.out.println(sql);
						}
						else if ((or_op < and_op)){
							if (isNumeric(query.substring(equal_pos+1, or_op))){
								sql = sql + query.substring(equal_pos+1, or_op) + " or ";
							}
							else{
								sql = sql + "'" + query.substring(equal_pos+1, or_op) + "'" + " or ";
							}
							query = query.substring(or_op+2);
						}
					}
					else{
						if (isNumeric(query.substring(equal_pos+1))){
							sql = sql + query.substring(equal_pos+1);
						}
						else{
							sql = sql + "'" + query.substring(equal_pos+1) + "'";
						}
						query = null;
					}
				}// End of while loop
				System.out.println(sql);
				list = conn.createQuery(sql)
						.executeAndFetch(City.class);
			}// End of outermost if
			
			System.out.println(sql);
			
			return list;
		}
	}
	public static City getItem(int city_id, String tableName, String field){
		String sql = "select ";
		
		//System.out.println("Table Name is " + tableName);
		
		String[] tokens = null;
		
		if (field == null){
			sql += "* ";
		}
		else{
			tokens = field.split(",");
			for (String token : tokens){
				sql = sql + token + ", ";
			}
			sql = sql.substring(0, sql.length()-2); // To get rid of the extra comma
			sql += " ";
		}
		
		sql = sql + "from " + tableName + " where city_id = :city_id";
		
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			//sql = "select * from customer where customer_id = :customer_id";
			List<City> list = conn.createQuery(sql)
					.addParameter("city_id", city_id)
					.executeAndFetch(City.class);
			if (list.size() == 0)
				return null;
			else
				return list.get(0);
		}
	}
	public static void create(City city) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "insert into city (city_id, city,  country_id, last_update) values (:city_id, :city, :country_id, :last_update)";
			conn.createQuery(sql)
					.addParameter("city_id", city.city_id)
					.addParameter("city", city.city)
					.addParameter("country_id", city.country_id)
					.addParameter("last_update", city.last_update)
					.executeUpdate();
			//city.city_id = conn.createQuery("select last_insert_rowid()").executeScalar(Integer.class);
		}
	}
	
	public static void delete(int city_id) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()){
			String sql = "delete from city where city_id = :city_id";
			conn.createQuery(sql).addParameter("city_id", city_id).executeUpdate();
		}
	}
	
	//Method to update City info, need sure if it works though, still require testing
	public static void update(City city) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()){
			String sql = "update city set city = :city, country_id = :country_id, last_update = :last_update where country_id = :country_id";
			conn.createQuery(sql)
					.addParameter("city", city.city)
					.addParameter("country_id", city.country_id)
					.addParameter("last_update", city.last_update)
					.addParameter("city_id", city.city_id)
					.executeUpdate();
		}
	}
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
