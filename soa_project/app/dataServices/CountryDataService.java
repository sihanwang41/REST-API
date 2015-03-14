package dataServices;
import java.util.List;

import models.Country;
import models.Country;
import models.Customer;
public class CountryDataService {
	public static List<Country> get(String query, String field, String tableName){
		List<Country> list = null;
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
							.executeAndFetch(Country.class);
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
						.executeAndFetch(Country.class);
			}// End of outermost if
			
			System.out.println(sql);
			
			return list;
		}
	}

	public static Country getItem(int country_id, String tableName, String field){
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
		
		sql = sql + "from " + tableName + " where country_id = :country_id";
		
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			//sql = "select * from customer where customer_id = :customer_id";
			List<Country> list = conn.createQuery(sql)
					.addParameter("country_id", country_id)
					.executeAndFetch(Country.class);
			if (list.size() == 0)
				return null;
			else
				return list.get(0);
		}
	}
	public static void create(Country country) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "insert into country (country, last_update) values (:country, :last_update)";
			conn.createQuery(sql)
					.addParameter("country", country.country)
					.addParameter("last_update", country.last_update)
					.executeUpdate();
			country.country_id = conn.createQuery("select last_insert_rowid()").executeScalar(Integer.class);
		}
	}
	
	public static void delete(int country_id) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()){
			String sql = "delete from country where country_id = :country_id";
			conn.createQuery(sql).addParameter("country_id", country_id).executeUpdate();
		}
	}
	
	//Method to update Country info, need sure if it works though, still require testing
	public static void update(Country country) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()){
			String sql = "update country set country = :country where country_id = :country_id";
			conn.createQuery(sql)	
					.addParameter("country", country.country)
					.addParameter("country_id", country.country_id)
					.executeUpdate();
		}
	}	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
