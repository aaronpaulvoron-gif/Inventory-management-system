/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package castroconstructionsuppliesNODB;

/**
 *
 * @author Gela
 */
public class AddProductPanel extends javax.swing.JPanel {

    /**
     * Creates new form AddProductPanel
     */
    public AddProductPanel() {
        initComponents();
    btnSaveProduct.addActionListener(e -> saveProduct());
    btnCancel.addActionListener(e -> clearForm());
    }
    private Runnable onSaved;

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void saveProduct() {

    String productName = txtProductName.getText().trim();
    String priceText = txtPrice.getText().trim();
    String stockText = txtStock.getText().trim();

    // Product name validation
    if (productName.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Please enter the product name.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        txtProductName.requestFocus();
        return;
    }

    // Price validation
    if (priceText.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Please enter the product price.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        txtPrice.requestFocus();
        return;
    }

    double price;

    try {
        price = Double.parseDouble(priceText);
    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Please enter a valid price.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        txtPrice.requestFocus();
        return;
    }

    if (price <= 0) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Price must be greater than zero.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        txtPrice.requestFocus();
        return;
    }

    // Unit validation
    if (CmbUnit.getSelectedIndex() <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Please select a unit.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        CmbUnit.requestFocus();
        return;
    }

    String unit = CmbUnit.getSelectedItem().toString();

    // Stock validation
    if (stockText.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Please enter the stock quantity.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        txtStock.requestFocus();
        return;
    }

    int stock;

    try {
        stock = Integer.parseInt(stockText);
    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Stock quantity must be a whole number.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        txtStock.requestFocus();
        return;
    }

    if (stock < 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Stock quantity cannot be negative.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        txtStock.requestFocus();
        return;
    }

    // Ask which category the product belongs to (loaded from the database)
    java.util.List<String> categories = CategoryDAO.findAllNames();
    String category = "";
    if (!categories.isEmpty()) {
        Object choice = javax.swing.JOptionPane.showInputDialog(
                this,
                "Select the product category:",
                "Category",
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                categories.toArray(),
                categories.get(0)
        );
        if (choice == null) {
            return; // cancelled
        }
        category = choice.toString();
    }

    // Save to the database (reorder level -1 = use the database default)
    Product newProduct = new Product(0, productName, category, unit, price, stock, -1);
    if (!ProductDAO.insert(newProduct)) {
        return;
    }

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Product \"" + productName + "\" was added successfully.",
            "Product Saved",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
    );

    if (onSaved != null) {
        onSaved.run();
    }

    java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
    if (window != null) {
        window.dispose();
    }
}
private void clearForm() {

    txtProductName.setText("");
    txtPrice.setText("");
    txtStock.setText("");

    CmbUnit.setSelectedIndex(0);

    txtProductName.requestFocus();
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblAddProductTitle = new javax.swing.JLabel();
        lblProductName = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        lblPrice = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        lblUnit = new javax.swing.JLabel();
        CmbUnit = new javax.swing.JComboBox<>();
        lblStock = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        btnSaveProduct = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(40, 40, 40));

        lblAddProductTitle.setFont(new java.awt.Font("Arial Hebrew", 2, 24)); // NOI18N
        lblAddProductTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblAddProductTitle.setText("ADD NEW PRODUCT");

        lblProductName.setForeground(new java.awt.Color(60, 60, 60));
        lblProductName.setText("PRODUCT NAME:");

        txtProductName.setForeground(new java.awt.Color(40, 40, 40));

        lblPrice.setForeground(new java.awt.Color(60, 60, 60));
        lblPrice.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPrice.setText("PRICE:");

        txtPrice.setForeground(new java.awt.Color(40, 40, 40));

        lblUnit.setForeground(new java.awt.Color(60, 60, 60));
        lblUnit.setText("UNIT:");

        CmbUnit.setForeground(new java.awt.Color(40, 40, 40));
        CmbUnit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Unit", "bag", "piece", "kg", "cubic meter", "sheet", "gallon", "roll", "set" }));

        lblStock.setForeground(new java.awt.Color(60, 60, 60));
        lblStock.setText("STOCK QUANTITY:");

        txtStock.setForeground(new java.awt.Color(40, 40, 40));

        btnSaveProduct.setBackground(new java.awt.Color(0, 0, 153));
        btnSaveProduct.setForeground(new java.awt.Color(255, 255, 255));
        btnSaveProduct.setText("SAVE PRODUCT");

        btnCancel.setBackground(new java.awt.Color(230, 230, 230));
        btnCancel.setForeground(new java.awt.Color(50, 50, 50));
        btnCancel.setText("CANCEL");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(btnSaveProduct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel)
                .addGap(92, 92, 92))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblAddProductTitle)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblProductName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblUnit)
                            .addComponent(lblStock))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtProductName)
                            .addComponent(txtPrice)
                            .addComponent(CmbUnit, 0, 111, Short.MAX_VALUE)
                            .addComponent(txtStock))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lblAddProductTitle)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductName)
                    .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrice)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnit)
                    .addComponent(CmbUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStock)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveProduct)
                    .addComponent(btnCancel))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CmbUnit;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSaveProduct;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAddProductTitle;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblStock;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
