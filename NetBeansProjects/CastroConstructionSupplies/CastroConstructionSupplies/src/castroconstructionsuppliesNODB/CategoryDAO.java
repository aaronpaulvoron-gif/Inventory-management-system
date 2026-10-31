package castroconstructionsuppliesNODB;

import java.util.Arrays;
import java.util.List;

/**
 * Category choices offered in the product forms. This is a fixed set of
 * options for the dropdown -- like the staff "position" list or the
 * login screen's "Staff / Customer" choice -- not stored business data,
 * so it isn't affected by "no hardcoded data".
 */
public class CategoryDAO {

    private static final List<String> CATEGORIES = Arrays.asList(
            "Construction Materials", "Concrete Products", "Steel & Metal",
            "Wood & Lumber", "Paint & Finishing", "Plumbing", "Electrical", "Tools"
    );

    public static List<String> findAllNames() {
        return CATEGORIES;
    }
}
