package castroconstructionsuppliesNODB;

/** One delivery of a product (a "batch"): how many came in, what each cost, and how many are left. */
public class StockBatch {

    private int id;
    private final int productId;
    private final String batchNo;
    private final int quantityReceived;
    private int quantityRemaining;
    private final double purchasePrice;
    private final String receivedDate; // yyyy-MM-dd
    private final String notes;

    public StockBatch(int id, int productId, String batchNo, int quantityReceived, int quantityRemaining,
            double purchasePrice, String receivedDate, String notes) {
        this.id = id;
        this.productId = productId;
        this.batchNo = batchNo;
        this.quantityReceived = quantityReceived;
        this.quantityRemaining = quantityRemaining;
        this.purchasePrice = purchasePrice;
        this.receivedDate = receivedDate;
        this.notes = notes == null ? "" : notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public String getBatchNo() { return batchNo; }
    public int getQuantityReceived() { return quantityReceived; }
    public int getQuantityRemaining() { return quantityRemaining; }
    public void setQuantityRemaining(int q) { this.quantityRemaining = Math.max(q, 0); }
    public double getPurchasePrice() { return purchasePrice; }
    public String getReceivedDate() { return receivedDate; }
    public String getNotes() { return notes; }
    public double getTotalCost() { return quantityReceived * purchasePrice; }
}
