package castroconstructionsuppliesNODB;

import java.util.ArrayList;

public class Sale {

    private String id;
    private String date;
    private String customerName;
    private String staffName;
    private ArrayList<SaleItem> items;
    private Integer customerId; // null = walk-in
    private Integer staffId;    // null = online order
    private String status = "Completed"; // Pending, Completed, Cancelled

    public Sale(String id, String date, String customerName, String staffName, ArrayList<SaleItem> items) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.staffName = staffName;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStaffName() {
        return staffName;
    }

    public ArrayList<SaleItem> getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;
        for (SaleItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
