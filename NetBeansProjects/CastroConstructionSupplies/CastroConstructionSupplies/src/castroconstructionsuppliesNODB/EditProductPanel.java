package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Edit Product form. Stock is shown but changed only through Stock-In (batches) and sales. */
public class EditProductPanel extends JPanel {

    private Product product;
    private Runnable onSaved;

    private final JTextField txtId = Theme.style(new JTextField());
    private final JTextField txtName = Theme.style(new JTextField());
    private final JComboBox<String> cmbCategory = ProductFormSupport.categoryCombo();
    private final JComboBox<String> cmbUnit = ProductFormSupport.unitCombo();
    private final JTextField txtPurchasePrice = Theme.style(new JTextField());
    private final JTextField txtPrice = Theme.style(new JTextField());   // selling price
    private final JTextField txtStock = Theme.style(new JTextField());
    private final JLabel lblProfit = new JLabel(" ");
    private final JLabel lblError = new JLabel(" ");

    public EditProductPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.SOFT);

        JPanel band = new JPanel(new BorderLayout());
        band.setBackground(Theme.NAVY);
        band.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));
        JLabel title = new JLabel("EDIT PRODUCT");
        title.setFont(Theme.HEADING);
        title.setForeground(Theme.ORANGE);
        band.add(title, BorderLayout.WEST);
        add(band, BorderLayout.NORTH);

        txtId.setEditable(false);
        txtStock.setEditable(false);
        txtId.setBackground(Theme.SOFT);
        txtStock.setBackground(Theme.SOFT);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(20, 26, 6, 26));

        txtId.setPreferredSize(new Dimension(200, 36));
        txtStock.setPreferredSize(new Dimension(200, 36));
        body.add(ProductFormSupport.pair(Theme.fieldBlock("Product ID", txtId, Theme.STEEL),
                Theme.fieldBlock("Current stock", txtStock, Theme.STEEL)));
        body.add(Box.createVerticalStrut(12));
        txtName.setPreferredSize(new Dimension(420, 36));
        body.add(Theme.fieldBlock("Product name", txtName, Theme.STEEL));
        body.add(Box.createVerticalStrut(12));
        cmbCategory.setPreferredSize(new Dimension(200, 36));
        cmbUnit.setPreferredSize(new Dimension(200, 36));
        body.add(ProductFormSupport.pair(Theme.fieldBlock("Category", cmbCategory, Theme.STEEL),
                Theme.fieldBlock("Unit", cmbUnit, Theme.STEEL)));
        body.add(Box.createVerticalStrut(12));
        txtPurchasePrice.setPreferredSize(new Dimension(200, 36));
        txtPrice.setPreferredSize(new Dimension(200, 36));
        body.add(ProductFormSupport.pair(Theme.fieldBlock("Purchase price (\u20B1)", txtPurchasePrice, Theme.STEEL),
                Theme.fieldBlock("Selling price (\u20B1)", txtPrice, Theme.STEEL)));
        body.add(Box.createVerticalStrut(6));
        lblProfit.setFont(Theme.LABEL);
        lblProfit.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblProfit);
        body.add(Box.createVerticalStrut(6));
        body.add(ProductFormSupport.small("To add stock, use Inventory > Stock-In (it records a batch)."));
        body.add(Box.createVerticalStrut(10));
        lblError.setFont(Theme.LABEL);
        lblError.setForeground(Theme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblError);
        add(body, BorderLayout.CENTER);

        JButton btnClear = Theme.secondaryButton("RESET");
        JButton btnUpdate = Theme.primaryButton("UPDATE PRODUCT");
        btnClear.addActionListener(e -> {
            if (product != null) {
                loadProduct(product, onSaved);
            }
        });
        btnUpdate.addActionListener(e -> updateProduct());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(6, 26, 20, 26));
        buttons.add(btnClear);
        buttons.add(btnUpdate);
        add(buttons, BorderLayout.SOUTH);

        ProductFormSupport.bindProfit(txtPurchasePrice, txtPrice, lblProfit);
    }

    /** Fills the form with the product being edited. */
    public void loadProduct(Product p, Runnable onSaved) {
        this.product = p;
        this.onSaved = onSaved;
        txtId.setText(p.getCode());
        txtName.setText(p.getName());
        selectOrAdd(cmbCategory, p.getCategory());
        selectOrAdd(cmbUnit, p.getUnit());
        txtPurchasePrice.setText(String.format("%.2f", p.getPurchasePrice()));
        txtPrice.setText(String.format("%.2f", p.getPrice()));
        txtStock.setText(p.getStockQuantity() + " " + p.getUnit());
        lblError.setText(" ");
        markInvalid(null);
    }

    private void selectOrAdd(JComboBox<String> combo, String value) {
        if (value == null || value.trim().isEmpty()) {
            combo.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(value)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.addItem(value);
        combo.setSelectedItem(value);
    }

    private void markInvalid(JTextField bad) {
        for (JTextField f : new JTextField[]{txtName, txtPurchasePrice, txtPrice}) {
            Theme.setInvalid(f, f == bad);
        }
    }

    private void fail(String message, JTextField field) {
        lblError.setText(message);
        markInvalid(field);
        if (field != null) {
            field.requestFocusInWindow();
        }
    }

    private void updateProduct() {
        if (product == null) {
            return;
        }
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            fail("Please enter the product name.", txtName);
            return;
        }
        if (cmbCategory.getSelectedIndex() <= 0) {
            fail("Please select a category.", null);
            return;
        }
        if (cmbUnit.getSelectedIndex() <= 0) {
            fail("Please select a unit.", null);
            return;
        }
        Double purchase = Theme.parseMoney(txtPurchasePrice.getText());
        if (purchase == null || purchase <= 0) {
            fail("Enter the purchase price (more than zero).", txtPurchasePrice);
            return;
        }
        Double selling = Theme.parseMoney(txtPrice.getText());
        if (selling == null || selling <= 0) {
            fail("Enter the selling price (more than zero).", txtPrice);
            return;
        }
        if (selling < purchase) {
            int ok = JOptionPane.showConfirmDialog(this,
                    String.format("The selling price (\u20B1%,.2f) is lower than the purchase price (\u20B1%,.2f).%n"
                            + "You would lose money on every unit sold. Save anyway?", selling, purchase),
                    "Check the prices", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) {
                return;
            }
        }
        product.setName(name);
        product.setCategory(cmbCategory.getSelectedItem().toString());
        product.setUnit(cmbUnit.getSelectedItem().toString());
        product.setPrice(selling);
        product.setPurchasePrice(purchase);
        if (!ProductDAO.update(product)) {
            fail("The product could not be updated. Please try again.", null);
            return;
        }
        JOptionPane.showMessageDialog(this, "Product updated successfully.",
                "Edit Product", JOptionPane.INFORMATION_MESSAGE);
        if (onSaved != null) {
            onSaved.run();
        }
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
}
