package dataServices;
import java.util.List;

import models.Customer;

public class CustomersDataService {
	
	public static List<Customer> get(){
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "select * from customer";
			return conn.createQuery(sql)
					.executeAndFetch(Customer.class);
		}
	}
	
	public static Customer get(int customer_id){
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "select * from customer where customer_id = :customer_id";
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
	
}
