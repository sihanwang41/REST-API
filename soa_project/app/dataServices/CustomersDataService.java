package dataServices;
import java.util.List;

import models.Customer;

public class CustomersDataService {
	
	public static List<Customer> get(){
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "select Id, FirstName, LastName from Customers";
			return conn.createQuery(sql)
					.executeAndFetch(Customer.class);
		}
	}
	
	public static Customer get(int id){
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "select Id, FirstName, LastName from Customers where Id = :Id";
			List<Customer> list = conn.createQuery(sql)
					.addParameter("Id", id)
					.executeAndFetch(Customer.class);
			if (list.size() == 0)
				return null;
			else
				return list.get(0);
		}
	}

	public static void create(Customer customer) {
		try (org.sql2o.Connection conn = DatabaseManager.sql2o.open()) {
			String sql = "insert into Customers (FirstName, LastName) values (:FirstName, :LastName)";
			conn.createQuery(sql)
					.addParameter("FirstName", customer.FirstName)
					.addParameter("LastName", customer.LastName)
					.executeUpdate();
			customer.Id = conn.createQuery("select last_insert_rowid()").executeScalar(Integer.class);
		}
	}
	
}
