package dataServices;
import java.util.List;

import models.Customer;

public class CustomersDataService {
	
	public static List<Customer> get(String query, String field, String tableName){
		List<Customer> list = null;
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
							.executeAndFetch(Customer.class);
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
						.executeAndFetch(Customer.class);
			}// End of outermost if
			
			System.out.println(sql);
			
			return list;
		}
	}
	
	public static Customer getItem(int customer_id, String tableName, String field){
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
		
		sql = sql + "from " + tableName + " where customer_id = :customer_id";
		
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			//sql = "select * from customer where customer_id = :customer_id";
			List<Customer> list = conn.createQuery(sql)
					.addParameter("customer_id", customer_id)
					.executeAndFetch(Customer.class);
			if (list.size() == 0)
				return null;
			else
				return list.get(0);
		}
	}

	public static void create(Customer customer) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "insert into Customer (store_id, first_name, last_name, address_id, email, active, create_date, last_update) values (:FirstName, :LastName)";
			conn.createQuery(sql)
					.addParameter("store_id", customer.store_id)
					.addParameter("first_name", customer.first_name)
					.addParameter("last_name", customer.last_name)
					.addParameter("address_id", customer.address_id)
					.addParameter("email", customer.email)
					.addParameter("active", customer.active)
					.addParameter("create_date", customer.create_date)
					.addParameter("last_update", customer.last_update)
					.executeUpdate();
			customer.customer_id = conn.createQuery("select last_insert_rowid()").executeScalar(Integer.class);
		}
	}
	
	public static void delete(int customer_id) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()){
			String sql = "delete from Customer where customer_id = :customer_id";
			conn.createQuery(sql).addParameter("customer_id", customer_id).executeUpdate();
		}
	}
	
	//Method to update customer info, need sure if it works though, still require testing
	public static void update(Customer customer) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()){
			String sql = "update customer set store_id = :store_id, first_name = :first_name, last_name = :last_name, address_id = :address_id, email = :email, active = :active, create_date = :create_date, last_update = :last_update where customer_id = :customer_id";
			conn.createQuery(sql)
					.addParameter("store_id", customer.store_id)
					.addParameter("first_name", customer.first_name)
					.addParameter("last_name", customer.last_name)
					.addParameter("address_id", customer.address_id)
					.addParameter("email", customer.email)
					.addParameter("active", customer.active)
					.addParameter("create_date", customer.create_date)
					.addParameter("last_update", customer.last_update)
					.addParameter("customer_id", customer.customer_id)
					.executeUpdate();
		}
	}
	
	// Method to check if the string is a number or not
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
