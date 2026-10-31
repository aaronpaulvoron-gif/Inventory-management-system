package castroconstructionsuppliesNODB;

public class Staff {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String position;
    private final String contact;
    private final String username;
    private final String status;

    public Staff(int id, String firstName, String lastName, String position, String contact,
            String username, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.contact = contact;
        this.username = username;
        this.status = status;
    }

    public int getId() { return id; }
    public String getCode() { return String.format("STF-%03d", id); }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    /** Display name only -- first and last name are stored separately. */
    public String getFullName() { return (firstName + " " + lastName).trim(); }
    public String getPosition() { return position; }
    public String getContact() { return contact; }
    public String getUsername() { return username; }
    public String getStatus() { return status; }
}
