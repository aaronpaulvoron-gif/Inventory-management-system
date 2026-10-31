package castroconstructionsuppliesNODB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Product catalog + stock levels, kept in memory for this run of the
 * program only. Nothing is pre-loaded -- the catalog starts empty and
 * only holds what is added through Products > Add Product while the
 * program is running. (Compare with the MySQL build, where the same
 * calls read and write real database rows instead of this list.)
 */
public class ProductDAO {

    private static final List<Product> products = new ArrayList<>();
    private static int nextId = 1;

    public static synchronized boolean insert(Product p) {
        p.setId(nextId++);
        if (p.getReorderLevel() < 0) {
            p.setReorderLevel(10); // same default with database version
        }
        products.add(p);
        // opening stock is recorded as the first batch so stock and batches always agree
        if (p.getStockQuantity() > 0) {
            Product saved = p;
            int opening = saved.getStockQuantity();
            saved.setStockQuantity(0);
            BatchDAO.addBatch(saved.getId(), "OPENING", opening, saved.getPurchasePrice(),
                    java.time.LocalDate.now().toString(), "Opening stock");
            saved.setPurchasePrice(p.getPurchasePrice());
        }
        return true;
    }

    public static synchronized boolean update(Product p) {
        Product existing = findById(p.getId());
        if (existing == null) {
            return false;
        }
        existing.setName(p.getName());
        existing.setCategory(p.getCategory());
        existing.setUnit(p.getUnit());
        existing.setPrice(p.getPrice());
        existing.setPurchasePrice(p.getPurchasePrice());
        // stock is changed through Stock-In (batches) and sales, so it is not overwritten here
        return true;
    }

    public static synchronized boolean delete(int id) {
        BatchDAO.deleteByProduct(id);
        return products.removeIf(p -> p.getId() == id);
    }

    public static synchronized boolean addStock(int id, int qty) {
        Product p = findById(id);
        if (p == null) {
            return false;
        }
        p.addStock(qty);
        return true;
    }

    public static synchronized boolean setReorderLevel(int id, int level) {
        Product p = findById(id);
        if (p == null) {
            return false;
        }
        p.setReorderLevel(level);
        return true;
    }

    public static synchronized Product findById(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /** Products whose name or category contains the text (empty = all), A-Z. */
    public static synchronized List<Product> search(String text) {
        String needle = text == null ? "" : text.trim().toLowerCase();
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (needle.isEmpty()
                    || p.getName().toLowerCase().contains(needle)
                    || p.getCategory().toLowerCase().contains(needle)) {
                result.add(p);
            }
        }
        result.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public static synchronized List<Product> findInStock() {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (!p.isOutOfStock()) {
                result.add(p);
            }
        }
        result.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public static synchronized List<Product> findLowStock() {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.isLowStock() || p.isOutOfStock()) {
                result.add(p);
            }
        }
        result.sort(Comparator.comparingInt(Product::getStockQuantity));
        return result;
    }

    public static synchronized int count() {
        return products.size();
    }

    public static synchronized long totalStockUnits() {
        long total = 0;
        for (Product p : products) {
            total += p.getStockQuantity();
        }
        return total;
    }
}
