package castroconstructionsuppliesNODB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sales history, kept in memory for this run of the program only.
 * A walk-in sale rung up by staff deducts stock immediately (status
 * Completed). An order placed online by a customer is saved as Pending
 * and joins a FIFO queue -- oldest first, in the order it was placed --
 * until a staff member confirms it (deducts stock, status Completed)
 * or cancels it (status Cancelled, no stock touched).
 */
public class SaleDAO {

    private static final List<Sale> sales = new ArrayList<>();
    private static int nextId = 1;

    public static synchronized String create(Sale sale, boolean completeImmediately) {
        if (completeImmediately) {
            for (SaleItem item : sale.getItems()) {
                Product p = ProductDAO.findById(item.getProductId());
                if (p == null || p.getStockQuantity() < item.getQuantity()) {
                    return "\"" + item.getProductName() + "\" no longer has enough stock.";
                }
            }
            for (SaleItem item : sale.getItems()) {
                ProductDAO.findById(item.getProductId()).deductStock(item.getQuantity());
                BatchDAO.deductFifo(item.getProductId(), item.getQuantity());
            }
        }
        sale.setId(code(nextId++));
        sale.setStatus(completeImmediately ? "Completed" : "Pending");
        sales.add(sale);
        return null;
    }

    /** Confirms a Pending order: deducts stock and marks it Completed. */
    public static synchronized String confirmOrder(String saleCode) {
        Sale sale = findByCode(saleCode);
        if (sale == null || !"Pending".equals(sale.getStatus())) {
            return "This order is no longer pending.";
        }
        for (SaleItem item : sale.getItems()) {
            Product p = ProductDAO.findById(item.getProductId());
            if (p == null || p.getStockQuantity() < item.getQuantity()) {
                return "\"" + item.getProductName() + "\" no longer has enough stock.";
            }
        }
        for (SaleItem item : sale.getItems()) {
            ProductDAO.findById(item.getProductId()).deductStock(item.getQuantity());
            BatchDAO.deductFifo(item.getProductId(), item.getQuantity());
        }
        sale.setStatus("Completed");
        return null;
    }

    /** Cancels a Pending order. No stock was ever deducted for it, so none is restored. */
    public static synchronized boolean cancelOrder(String saleCode) {
        return cancelOrder(saleCode, "");
    }

    /**
     * Staff cancel a Pending order and give the reason. If the order belongs to a registered
     * customer, that customer is notified (a pop-up the next time they open the app).
     */
    public static synchronized boolean cancelOrder(String saleCode, String reason) {
        Sale sale = findByCode(saleCode);
        if (sale == null || !"Pending".equals(sale.getStatus())) {
            return false;
        }
        sale.setStatus("Cancelled");
        sale.setCancelReason(reason);
        sale.setCancelledBy("Admin");
        sale.setCustomerNotified(sale.getCustomerId() == null); // walk-ins have no account to notify
        return true;
    }

    /** Orders staff cancelled that this customer has not been told about yet, oldest first. */
    public static synchronized List<Sale> findUnreadCancellations(Integer customerId) {
        List<Sale> result = new ArrayList<>();
        if (customerId == null) {
            return result;
        }
        for (Sale s : sales) {
            if (customerId.equals(s.getCustomerId()) && "Cancelled".equals(s.getStatus())
                    && "Admin".equals(s.getCancelledBy()) && !s.isCustomerNotified()) {
                result.add(s);
            }
        }
        return result;
    }

    /** Marks every staff cancellation of this customer as seen. */
    public static synchronized void markNotified(Integer customerId) {
        if (customerId == null) {
            return;
        }
        for (Sale s : sales) {
            if (customerId.equals(s.getCustomerId()) && "Cancelled".equals(s.getStatus())) {
                s.setCustomerNotified(true);
            }
        }
    }

    /**
     * A customer cancels THEIR OWN order while it is still Pending. Returns null on success,
     * otherwise a message to show. The reason is saved with the order.
     */
    public static synchronized String cancelOwnOrder(String saleCode, Integer customerId, String reason) {
        Sale sale = findByCode(saleCode);
        if (sale == null || customerId == null || !customerId.equals(sale.getCustomerId())) {
            return "You can only cancel your own orders.";
        }
        if (!"Pending".equals(sale.getStatus())) {
            return "Only orders that are still waiting to be confirmed can be cancelled.";
        }
        sale.setStatus("Cancelled");
        sale.setCancelReason(reason);
        sale.setCancelledBy("Customer");
        sale.setCustomerNotified(true);
        return null;
    }

    /** Pending orders, oldest first -- the order staff should process them in. */
    public static synchronized List<Sale> findQueue() {
        List<Sale> result = new ArrayList<>();
        for (Sale s : sales) {
            if ("Pending".equals(s.getStatus())) {
                result.add(s); // sales list is already oldest-first by insertion order
            }
        }
        return result;
    }

    public static synchronized int queueCount() {
        int count = 0;
        for (Sale s : sales) {
            if ("Pending".equals(s.getStatus())) {
                count++;
            }
        }
        return count;
    }

    /** All sales, newest first. Pass a customer id to see only that customer's orders. */
    public static synchronized List<Sale> findAll(Integer customerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale s : sales) {
            if (customerId == null || customerId.equals(s.getCustomerId())) {
                result.add(s);
            }
        }
        Collections.reverse(result);
        return result;
    }

    public static synchronized List<Sale> findRecent(int max) {
        List<Sale> all = findAll(null);
        if (all.size() > max) {
            return new ArrayList<>(all.subList(0, max));
        }
        return all;
    }

    /** Sum of Completed sales only -- Pending and Cancelled don't count as revenue yet. */
    public static synchronized double totalAmount() {
        double total = 0;
        for (Sale s : sales) {
            if ("Completed".equals(s.getStatus())) {
                total += s.getTotal();
            }
        }
        return total;
    }

    public static String code(int id) {
        return String.format("TXN-%05d", id);
    }

    private static Sale findByCode(String saleCode) {
        for (Sale s : sales) {
            if (s.getId().equals(saleCode)) {
                return s;
            }
        }
        return null;
    }
}
