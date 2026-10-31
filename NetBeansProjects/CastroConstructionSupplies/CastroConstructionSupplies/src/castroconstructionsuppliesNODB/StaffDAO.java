package castroconstructionsuppliesNODB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Staff accounts, kept in memory for this run of the program only.
 * No staff member is pre-loaded here -- the only built-in login in the
 * whole program is the "admin" / "admin123" account handled directly by
 * LoginFrame (see Session.ADMIN_USERNAME / ADMIN_PASSWORD). Every staff
 * member added from the Staff screen lives only in this list, and is
 * gone once the program is closed.
 */
public class StaffDAO {

    private static final List<Staff> staffList = new ArrayList<>();
    private static final Map<Integer, String> passwordHashes = new HashMap<>();
    private static int nextId = 1;

    public static synchronized int count() {
        return staffList.size();
    }

    public static synchronized boolean usernameExists(String username, int excludeId) {
        for (Staff s : staffList) {
            if (s.getUsername().equalsIgnoreCase(username) && s.getId() != excludeId) {
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean insert(String firstName, String lastName, String position, String contact,
            String username, String password) {
        if (usernameExists(username, 0)) {
            return false;
        }
        int id = nextId++;
        staffList.add(new Staff(id, firstName, lastName, position, contact, username, "Active"));
        passwordHashes.put(id, PasswordUtil.hash(password));
        return true;
    }

    /** Only Active staff may log in. Returns null on failure. */
    public static synchronized Staff authenticate(String username, String password) {
        for (Staff s : staffList) {
            if (s.getUsername().equalsIgnoreCase(username) && "Active".equals(s.getStatus())) {
                String hash = passwordHashes.get(s.getId());
                if (hash != null && PasswordUtil.verify(password, hash)) {
                    return s;
                }
            }
        }
        return null;
    }

    public static synchronized List<Staff> search(String text) {
        String needle = text == null ? "" : text.trim().toLowerCase();
        List<Staff> result = new ArrayList<>();
        for (Staff s : staffList) {
            if (needle.isEmpty()
                    || s.getFullName().toLowerCase().contains(needle)
                    || s.getPosition().toLowerCase().contains(needle)
                    || s.getCode().toLowerCase().contains(needle)) {
                result.add(s);
            }
        }
        return result;
    }

    public static synchronized boolean update(int id, String firstName, String lastName, String contact, String username) {
        for (int i = 0; i < staffList.size(); i++) {
            Staff s = staffList.get(i);
            if (s.getId() == id) {
                staffList.set(i, new Staff(id, firstName, lastName, s.getPosition(), contact, username, s.getStatus()));
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean updatePositionAndStatus(int id, String position, String status) {
        for (int i = 0; i < staffList.size(); i++) {
            Staff s = staffList.get(i);
            if (s.getId() == id) {
                staffList.set(i, new Staff(id, s.getFirstName(), s.getLastName(), position, s.getContact(), s.getUsername(), status));
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean delete(int id) {
        passwordHashes.remove(id);
        return staffList.removeIf(s -> s.getId() == id);
    }

    /** Sets a new password for a staff account (salted-hashed). */
    public static synchronized boolean resetPassword(int id, String newPassword) {
        for (Staff s : staffList) {
            if (s.getId() == id) {
                passwordHashes.put(id, PasswordUtil.hash(newPassword));
                return true;
            }
        }
        return false;
    }
}
