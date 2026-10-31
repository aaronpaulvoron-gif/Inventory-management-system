package castroconstructionsuppliesNODB;

public class Customer {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String contact;

    public Customer(String firstName, String lastName, String email, String username,
                    String password, String contact) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.contact = contact;
    }

    /** Used when loading an existing customer from storage. */
    public Customer(int id, String firstName, String lastName, String email, String username, String contact) {
        this(firstName, lastName, email, username, null, contact);
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /** Display name only -- the system stores first and last name separately. */
    public String getFullName() {
        return (firstName + " " + lastName).trim();
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
