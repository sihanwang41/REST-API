package models;

public class Customer {
    public Customer(int id, String firstName, String lastName) {
        this.Id = id;
        this.FirstName = firstName;
        this.LastName = lastName;
    }

    public int Id;
    public String FirstName;
    public String LastName;
}