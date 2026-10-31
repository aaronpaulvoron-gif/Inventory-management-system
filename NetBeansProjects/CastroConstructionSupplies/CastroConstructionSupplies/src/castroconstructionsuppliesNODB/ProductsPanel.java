/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package castroconstructionsuppliesNODB;

/**
 *
 * @author USER
 */
public class ProductsPanel extends javax.swing.JPanel {

    /**
     * Creates new form ProductsPanel
     */
    private final java.util.ArrayList<Product> shown = new java.util.ArrayList<>();

    private final javax.swing.JComboBox<String> cmbCategory = new javax.swing.JComboBox<>();

    // table column positions (staff see purchase price and reorder level, customers do not)
    private int colCategory, colPurchase, colSelling, colStock, colStatus;

    public ProductsPanel() {
        initComponents();

        boolean staff = !Session.isCustomer();
        String[] columns = staff
                ? new String[]{"Product Name", "Category", "Purchase Price", "Selling Price", "Unit", "Stock", "Reorder", "Status"}
                : new String[]{"Product Name", "Category", "Price", "Unit", "Stock", "Status"};
        colCategory = 1;
        colPurchase = staff ? 2 : -1;
        colSelling = staff ? 3 : 2;
        colStock = staff ? 5 : 4;
        colStatus = staff ? 7 : 5;

        tblProducts.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tblProducts.setRowHeight(30);
        tblProducts.getTableHeader().setReorderingAllowed(false);
        tblProducts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProducts.setAutoCreateRowSorter(true);
        tblProducts.getColumnModel().getColumn(colSelling).setCellRenderer(new CurrencyRenderer());
        if (staff) {
            tblProducts.getColumnModel().getColumn(colPurchase).setCellRenderer(new CurrencyRenderer());
        }
        tblProducts.getColumnModel().getColumn(colStatus).setCellRenderer(new StatusRenderer());
        tblProducts.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] widths = staff ? new int[]{230, 170, 105, 105, 85, 60, 70, 110}
                             : new int[]{260, 190, 110, 90, 70, 120};
        for (int i = 0; i < widths.length; i++) {
            tblProducts.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // category filter: "All Categories" + every category in the product forms
        cmbCategory.addItem("All Categories");
        for (String c : CategoryDAO.findAllNames()) {
            cmbCategory.addItem(c);
        }
        cmbCategory.addActionListener(e -> refreshTable());

        btnSearch.addActionListener(e -> refreshTable());
        txtSearch.addActionListener(e -> refreshTable());

        // Customers can browse products but not change the catalog
        if (Session.isCustomer()) {
            btnAddProduct.setVisible(false);
            btnEditProduct.setVisible(false);
            btnDeleteProduct.setVisible(false);
        }

        rebuildLayout();
        refreshTable();
    }

    private static void styleButton(javax.swing.JButton b, java.awt.Color bg) {
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    /**
     * Re-arranges the form's existing components (title, search, category filter, table, buttons)
     * with a layout that stretches to the window, so nothing overlaps or runs off the screen.
     */
    private void rebuildLayout() {
        removeAll();
        setLayout(new java.awt.BorderLayout());
        setBackground(Theme.SOFT);

        lblTitle.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        lblTitle.setForeground(new java.awt.Color(20, 35, 60));
        jLabel1.setText(Session.isCustomer() ? "Browse products by category" : "Manage products, prices and categories");
        jLabel1.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        jLabel1.setForeground(new java.awt.Color(100, 100, 100));
        lblSearch.setFont(Theme.BODY);
        lblSearch.setText("Search:");
        txtSearch.setPreferredSize(new java.awt.Dimension(200, 34));
        cmbCategory.setPreferredSize(new java.awt.Dimension(190, 34));
        styleButton(btnSearch, Theme.STEEL);
        styleButton(btnAddProduct, Theme.ORANGE);
        styleButton(btnEditProduct, Theme.STEEL);
        styleButton(btnDeleteProduct, Theme.DANGER);
        Theme.styleTable(tblProducts);
        tblProducts.setRowHeight(30);

        javax.swing.JPanel titles = new javax.swing.JPanel();
        titles.setLayout(new javax.swing.BoxLayout(titles, javax.swing.BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        titles.add(lblTitle);
        titles.add(javax.swing.Box.createVerticalStrut(4));
        titles.add(jLabel1);

        javax.swing.JPanel filters = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);
        filters.add(lblSearch);
        filters.add(txtSearch);
        filters.add(btnSearch);
        filters.add(javax.swing.Box.createHorizontalStrut(10));
        javax.swing.JLabel lblCat = new javax.swing.JLabel("Category:");
        lblCat.setFont(Theme.BODY);
        filters.add(lblCat);
        filters.add(cmbCategory);

        javax.swing.JPanel north = new javax.swing.JPanel(new java.awt.BorderLayout(0, 14));
        north.setOpaque(false);
        north.add(titles, java.awt.BorderLayout.NORTH);
        north.add(filters, java.awt.BorderLayout.SOUTH);

        scrollPaneProducts.setBorder(javax.swing.BorderFactory.createLineBorder(Theme.BORDER));
        scrollPaneProducts.getViewport().setBackground(java.awt.Color.WHITE);

        javax.swing.JPanel buttons = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(btnEditProduct);
        buttons.add(btnDeleteProduct);
        buttons.add(btnAddProduct);

        javax.swing.JPanel root = new javax.swing.JPanel(new java.awt.BorderLayout(0, 14));
        root.setOpaque(false);
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 25, 20, 25));
        root.add(north, java.awt.BorderLayout.NORTH);
        root.add(scrollPaneProducts, java.awt.BorderLayout.CENTER);
        root.add(buttons, java.awt.BorderLayout.SOUTH);
        add(root, java.awt.BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /** Reloads the table from the shared catalog, applying the search box and the category filter. */
    public void refreshTable() {
        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblProducts.getModel();
        model.setRowCount(0);
        shown.clear();
        String filter = txtSearch.getText().trim();
        Object picked = cmbCategory.getSelectedItem();
        String category = (picked == null || cmbCategory.getSelectedIndex() <= 0) ? null : picked.toString();

        java.util.List<Product> list = new java.util.ArrayList<>();
        for (Product p : ProductDAO.search(filter)) {
            if (category == null || category.equalsIgnoreCase(p.getCategory())) {
                list.add(p);
            }
        }
        // products are grouped by category, then A-Z by name
        list.sort(java.util.Comparator.comparing(Product::getCategory, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getName, String.CASE_INSENSITIVE_ORDER));

        for (Product p : list) {
            shown.add(p);
            if (Session.isCustomer()) {
                model.addRow(new Object[]{p.getName(), p.getCategory(), p.getPrice(), p.getUnit(),
                    p.getStockQuantity(), p.getStockStatus()});
            } else {
                model.addRow(new Object[]{p.getName(), p.getCategory(), p.getPurchasePrice(), p.getPrice(),
                    p.getUnit(), p.getStockQuantity(), p.getReorderLevel(), p.getStockStatus()});
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlProducts = new javax.swing.JPanel();
        scrollPaneProducts = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        btnSearch = new javax.swing.JButton();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnAddProduct = new javax.swing.JButton();
        btnEditProduct = new javax.swing.JButton();
        btnDeleteProduct = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        pnlHeader.setBackground(new java.awt.Color(240, 243, 245));
        pnlHeader.setForeground(new java.awt.Color(255, 255, 255));

        lblTitle.setBackground(new java.awt.Color(38, 50, 56));
        lblTitle.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(51, 51, 51));
        lblTitle.setText("PRODUCTS");

        pnlProducts.setBackground(new java.awt.Color(240, 243, 245));
        pnlProducts.setForeground(new java.awt.Color(240, 243, 245));

        scrollPaneProducts.setBackground(new java.awt.Color(240, 243, 245));
        scrollPaneProducts.setForeground(new java.awt.Color(255, 255, 255));

        tblProducts.setForeground(new java.awt.Color(40, 40, 40));
        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Product Name ", "Price", "Unit", "Stock", "Reorder", "Status", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPaneProducts.setViewportView(tblProducts);

        btnSearch.setBackground(new java.awt.Color(52, 152, 219));
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("SEARCH");

        lblSearch.setFont(new java.awt.Font("Arial Hebrew", 2, 14)); // NOI18N
        lblSearch.setText("Search Product:");

        btnAddProduct.setBackground(new java.awt.Color(52, 152, 219));
        btnAddProduct.setForeground(new java.awt.Color(255, 255, 255));
        btnAddProduct.setText("ADD PRODUCT");
        btnAddProduct.addActionListener(this::btnAddProductActionPerformed);

        btnEditProduct.setBackground(new java.awt.Color(255, 153, 0));
        btnEditProduct.setText("EDIT SELECTED");
        btnEditProduct.addActionListener(this::btnEditProductActionPerformed);

        btnDeleteProduct.setText("DELETE SELECTED");
        btnDeleteProduct.addActionListener(this::btnDeleteProductActionPerformed);

        jLabel1.setFont(new java.awt.Font("Arial Hebrew", 3, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Manage Products");

        javax.swing.GroupLayout pnlProductsLayout = new javax.swing.GroupLayout(pnlProducts);
        pnlProducts.setLayout(pnlProductsLayout);
        pnlProductsLayout.setHorizontalGroup(
            pnlProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlProductsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnEditProduct)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteProduct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddProduct)
                .addGap(38, 38, 38))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlProductsLayout.createSequentialGroup()
                .addGroup(pnlProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlProductsLayout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addGroup(pnlProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlProductsLayout.createSequentialGroup()
                                .addComponent(lblSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlProductsLayout.createSequentialGroup()
                                .addGap(151, 151, 151)
                                .addComponent(jLabel1)))
                        .addGap(18, 18, 18)
                        .addComponent(btnSearch))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlProductsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPaneProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 933, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        pnlProductsLayout.setVerticalGroup(
            pnlProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlProductsLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(52, 52, 52)
                .addGroup(pnlProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addGap(18, 18, 18)
                .addComponent(scrollPaneProducts, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addGap(32, 32, 32)
                .addGroup(pnlProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGap(335, 335, 335)
                        .addComponent(lblTitle)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProductActionPerformed
       AddProductPanel addProduct = new AddProductPanel();
    addProduct.setOnSaved(this::refreshTable);

    javax.swing.JFrame frame = new javax.swing.JFrame("Add Product");
    frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
    frame.setContentPane(addProduct);
    frame.pack();
    frame.setLocationRelativeTo(this);
    frame.setVisible(true);
    }//GEN-LAST:event_btnAddProductActionPerformed

    private void btnEditProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditProductActionPerformed
int selectedRow = tblProducts.getSelectedRow();
    if (selectedRow != -1) { selectedRow = tblProducts.convertRowIndexToModel(selectedRow); }

    if (selectedRow == -1) {
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "Please select a product first.",
            "Edit Product",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    EditProductPanel editProduct = new EditProductPanel();
    editProduct.loadProduct(shown.get(selectedRow), this::refreshTable);

    javax.swing.JFrame frame =
        new javax.swing.JFrame("Edit Product");

    frame.setDefaultCloseOperation(
        javax.swing.JFrame.DISPOSE_ON_CLOSE
    );

    frame.setContentPane(editProduct);
    frame.pack();
    frame.setLocationRelativeTo(this);
    frame.setVisible(true);       
    }//GEN-LAST:event_btnEditProductActionPerformed

    private void btnDeleteProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteProductActionPerformed
       int selectedRow = tblProducts.getSelectedRow();
    if (selectedRow != -1) { selectedRow = tblProducts.convertRowIndexToModel(selectedRow); }

    if (selectedRow == -1) {
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "Please select a product first.",
            "Delete Product",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    int answer = javax.swing.JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete this product?",
        "Delete Product",
        javax.swing.JOptionPane.YES_NO_OPTION
    );

    if (answer == javax.swing.JOptionPane.YES_OPTION) {

        if (ProductDAO.delete(shown.get(selectedRow).getId())) {
            refreshTable();
        }

        javax.swing.JOptionPane.showMessageDialog(
            this,
            "Product deleted successfully.",
            "Delete Product",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }
    }//GEN-LAST:event_btnDeleteProductActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddProduct;
    private javax.swing.JButton btnDeleteProduct;
    private javax.swing.JButton btnEditProduct;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlProducts;
    private javax.swing.JScrollPane scrollPaneProducts;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
