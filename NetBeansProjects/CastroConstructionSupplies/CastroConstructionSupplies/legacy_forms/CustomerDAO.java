package castroconstructionsuppliesNODB;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer accounts, kept in memory for this run of the program only.
 * No customer is pre-loaded -- the list starts empty and only holds
 * whoever registers through Customer Registration while the program
 * runs. Passwords are still salted-hashed, never stored as plain text.
 */
public class CustomerDAO {

    private static final List<Customer> customers = new ArrayList<>();
    private static int nextId = 1;

    public static synchronized boolean usernameExists(String username) {
        for (Customer c : customers) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /** Saves a new customer; the password on the object is hashed before storing. */
    public static synchronized boolean insert(Customer customer) {
        if (usernameExists(customer.getUsername())) {
            return false;
        }
        Customer stored = new Customer(customer.getFullName(), customer.getEmail(),
                customer.getUsername(), PasswordUtil.hash(customer.getPassword()), customer.getContact());
        stored.setId(nextId++);
        customers.add(stored);
        return true;
    }

    /** Returns the customer when username + password match, otherwise null. */
    public static synchronized Customer authenticate(String username, String password) {
        for (Customer c : customers) {
            if (c.getUsername().equalsIgnoreCase(username) && PasswordUtil.verify(password, c.getPassword())) {
                return c;
            }
        }
        return null;
    }

    public static synchronized List<Customer> search(String text) {
        String needle = text == null ? "" : text.trim().toLowerCase();
        List<Customer> result = new ArrayList<>();
        for (Customer c : customers) {
            if (needle.isEmpty()
                    || c.getFullName().toLowerCase().contains(needle)
                    || c.getUsername().toLowerCase().contains(needle)
                    || c.getCode().toLowerCase().contains(needle)) {
                result.add(c);
            }
        }
        return result;
    }

    public static synchronized boolean update(int id, String fullName, String contact, String email) {
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            if (c.getId() == id) {
                Customer updated = new Customer(fullName, email, c.getUsername(), c.getPassword(), contact);
                updated.setId(id);
                customers.set(i, updated);
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean delete(int id) {
        return customers.removeIf(c -> c.getId() == id);
    }

    public static synchronized int count() {
        return customers.size();
    }
}
