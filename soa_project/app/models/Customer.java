package models;

public class Customer {
    public Customer(int customer_id, int store_id, String first_name, String last_name, String email, String active, int address_id, String create_date, String last_update) {
        this.customer_id = customer_id;
        this.store_id = store_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.active = active;
        this.address_id = address_id;
        this.create_date = create_date;
        this.last_update = last_update;
    }

    public int customer_id;
    public int store_id;
    public String first_name;
    public String last_name;
    public String email;
    public String active;
    public int address_id;
    public String create_date;
    public String last_update;
}