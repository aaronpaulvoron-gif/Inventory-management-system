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
    private String cancelReason = "";    // filled in when an order is cancelled
    private String cancelledBy = "";     // "Admin" or "Customer"
    private boolean customerNotified = true; // false = the customer has not yet seen a staff cancellation

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

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy == null ? "" : cancelledBy;
    }

    public boolean isCustomerNotified() {
        return customerNotified;
    }

    public void setCustomerNotified(boolean customerNotified) {
        this.customerNotified = customerNotified;
    }

    /** Text for the history table, e.g. "Admin: Out of stock". Empty unless the sale was cancelled. */
    public String getCancelSummary() {
        if (!"Cancelled".equals(status)) {
            return "";
        }
        String who = cancelledBy.isEmpty() ? "" : cancelledBy;
        if (cancelReason.isEmpty()) {
            return who;
        }
        return who.isEmpty() ? cancelReason : who + ": " + cancelReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? "" : cancelReason;
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
