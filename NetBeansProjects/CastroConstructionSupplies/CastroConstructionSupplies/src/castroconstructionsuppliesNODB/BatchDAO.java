package castroconstructionsuppliesNODB;

import java.util.ArrayList;
import java.util.List;

/**
 * Stock batches (deliveries), kept in memory for this run of the program.
 * Stock-in = add a batch: the product's stock goes up by the batch quantity and its
 * purchase price becomes the latest batch cost. Sales use the OLDEST batch first (FIFO).
 */
public class BatchDAO {

    private static final List<StockBatch> batches = new ArrayList<>();
    private static int nextId = 1;

    /** Suggested batch number for the next delivery of this product, e.g. B-0003. */
    public static synchronized String suggestBatchNo(int productId) {
        int count = 0;
        for (StockBatch b : batches) {
            if (b.getProductId() == productId) {
                count++;
            }
        }
        return String.format("B-%04d", count + 1);
    }

    public static synchronized boolean batchNoExists(int productId, String batchNo) {
        for (StockBatch b : batches) {
            if (b.getProductId() == productId && b.getBatchNo().equalsIgnoreCase(batchNo.trim())) {
                return true;
            }
        }
        return false;
    }

    /** Records a delivery and adds it to the product's stock. Returns null on success, else a message. */
    public static synchronized String addBatch(int productId, String batchNo, int qty,
            double purchasePrice, String receivedDate, String notes) {
        Product p = ProductDAO.findById(productId);
        if (p == null) {
            return "That product no longer exists.";
        }
        if (batchNoExists(productId, batchNo)) {
            return "Batch number \"" + batchNo.trim() + "\" is already used for this product.";
        }
        StockBatch b = new StockBatch(nextId++, productId, batchNo.trim(), qty, qty,
                purchasePrice, receivedDate, notes);
        batches.add(b);
        p.addStock(qty);
        p.setPurchasePrice(purchasePrice);
        return null;
    }

    /** Batches of one product, newest first. */
    public static synchronized List<StockBatch> findByProduct(int productId) {
        List<StockBatch> result = new ArrayList<>();
        for (StockBatch b : batches) {
            if (b.getProductId() == productId) {
                result.add(b);
            }
        }
        java.util.Collections.reverse(result);
        return result;
    }

    /** Takes units out of the oldest batches first (called when a sale is completed). */
    public static synchronized void deductFifo(int productId, int qty) {
        for (StockBatch b : batches) {
            if (qty <= 0) {
                return;
            }
            if (b.getProductId() == productId && b.getQuantityRemaining() > 0) {
                int take = Math.min(qty, b.getQuantityRemaining());
                b.setQuantityRemaining(b.getQuantityRemaining() - take);
                qty -= take;
            }
        }
    }

    /** Removes the batches of a deleted product. */
    public static synchronized void deleteByProduct(int productId) {
        batches.removeIf(b -> b.getProductId() == productId);
    }
}
