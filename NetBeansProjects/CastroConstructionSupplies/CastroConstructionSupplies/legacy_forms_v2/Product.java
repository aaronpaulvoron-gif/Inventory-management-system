package castroconstructionsuppliesNODB;

/**
 * Represents a single product/item sold by Castro's Construction Supplies.
 * Stock quantity and reorder level live here so the Products, Inventory,
 * Sales, and Dashboard panels all read from the same source of truth.
 */
public class Product {

    private int id;
    private String name;
    private String category;
    private String unit;
    private double price;
    private int stockQuantity;
    private int reorderLevel;

    public Product(int id, String name, String category, String unit,
            double price, int stockQuantity, int reorderLevel) {
        this.id = id;
        this.name = name;
        this.category = category == null ? "" : category;
        this.unit = unit;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** Display code such as P-0007. */
    public String getCode() {
        return String.format("P-%04d", id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void addStock(int amount) {
        this.stockQuantity += amount;
    }

    public void deductStock(int amount) {
        this.stockQuantity -= amount;
        if (this.stockQuantity < 0) {
            this.stockQuantity = 0;
        }
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public boolean isOutOfStock() {
        return stockQuantity <= 0;
    }

    public boolean isLowStock() {
        return stockQuantity > 0 && stockQuantity <= reorderLevel;
    }

    public String getStockStatus() {
        if (isOutOfStock()) {
            return "OUT OF STOCK";
        } else if (isLowStock()) {
            return "LOW STOCK";
        } else {
            return "IN STOCK";
        }
    }
}
