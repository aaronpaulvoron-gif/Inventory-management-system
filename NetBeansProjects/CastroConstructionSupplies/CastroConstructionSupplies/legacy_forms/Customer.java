/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package castroconstructionsuppliesNODB;

public class Customer {

    private int id;
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String contact;

    public Customer(String fullName, String email, String username,
                    String password, String contact) {

        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.contact = contact;
    }

    /** Used when loading an existing customer from the database. */
    public Customer(int id, String fullName, String email, String username, String contact) {
        this(fullName, email, username, null, contact);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return String.format("CUS-%03d", id);
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getContact() {
        return contact;
    }
}
