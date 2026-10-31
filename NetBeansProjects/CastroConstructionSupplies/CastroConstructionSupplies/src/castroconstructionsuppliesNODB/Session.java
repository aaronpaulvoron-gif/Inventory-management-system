package castroconstructionsuppliesNODB;

/**
 * Tiny holder for "who is logged in right now" so panels (Sales, header
 * greeting, sidebar visibility) can react to the account type without
 * threading parameters through every constructor.
 */
public class Session {

    public static final String ROLE_STAFF = "Admin"; // the store-side role (the name of the constant is kept so no code breaks)
    public static final String ROLE_CUSTOMER = "Customer";

    /** The one built-in admin login. Every other account lives in the database. */
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";

    public static String currentUserName = "Guest";
    public static String currentUserRole = ROLE_STAFF;
    public static Integer currentStaffId = null;
    public static Integer currentCustomerId = null;

    public static boolean isStaff() {
        return ROLE_STAFF.equals(currentUserRole);
    }

    public static boolean isCustomer() {
        return ROLE_CUSTOMER.equals(currentUserRole);
    }
}
