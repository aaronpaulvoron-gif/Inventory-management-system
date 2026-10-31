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

/**
 * Add Product form: name, category, unit, purchase price (what we pay), selling price (what the
 * customer pays) and the opening stock, which is saved as the product's first batch.
 */
public class AddProductPanel extends JPanel {

    private final JTextField txtProductName = Theme.style(new JTextField());
    private final JComboBox<String> cmbCategory = ProductFormSupport.categoryCombo();
    private final JComboBox<String> cmbUnit = ProductFormSupport.unitCombo();
    private final JTextField txtPurchasePrice = Theme.style(new JTextField());
    private final JTextField txtPrice = Theme.style(new JTextField());   // selling price
    private final JTextField txtStock = Theme.style(new JTextField("0"));
    private final JLabel lblProfit = new JLabel(" ");
    private final JLabel lblError = new JLabel(" ");
    private Runnable onSaved;

    public AddProductPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.SOFT);

        JPanel band = new JPanel(new BorderLayout());
        band.setBackground(Theme.NAVY);
        band.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));
        JLabel title = new JLabel("ADD PRODUCT");
        title.setFont(Theme.HEADING);
        title.setForeground(Theme.ORANGE);
        band.add(title, BorderLayout.WEST);
        add(band, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(20, 26, 6, 26));

        txtProductName.setPreferredSize(new Dimension(420, 36));
        body.add(Theme.fieldBlock("Product name", txtProductName, Theme.STEEL));
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
        body.add(Box.createVerticalStrut(8));
        txtStock.setPreferredSize(new Dimension(200, 36));
        body.add(ProductFormSupport.pair(Theme.fieldBlock("Opening stock (first batch)", txtStock, Theme.STEEL),
                new JPanel() {{ setOpaque(false); }}));
        body.add(Box.createVerticalStrut(6));
        body.add(ProductFormSupport.small("Add more stock later with Inventory > Stock-In (each delivery is a batch)."));
        body.add(Box.createVerticalStrut(10));
        lblError.setFont(Theme.LABEL);
        lblError.setForeground(Theme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblError);
        add(body, BorderLayout.CENTER);

        JButton btnClear = Theme.secondaryButton("CLEAR");
        JButton btnSave = Theme.primaryButton("SAVE PRODUCT");
        btnClear.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveProduct());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(6, 26, 20, 26));
        buttons.add(btnClear);
        buttons.add(btnSave);
        add(buttons, BorderLayout.SOUTH);

        ProductFormSupport.bindProfit(txtPurchasePrice, txtPrice, lblProfit);
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void clearForm() {
        txtProductName.setText("");
        cmbCategory.setSelectedIndex(0);
        cmbUnit.setSelectedIndex(0);
        txtPurchasePrice.setText("");
        txtPrice.setText("");
        txtStock.setText("0");
        lblError.setText(" ");
        markInvalid(null);
    }

    private void markInvalid(JTextField bad) {
        for (JTextField f : new JTextField[]{txtProductName, txtPurchasePrice, txtPrice, txtStock}) {
            Theme.setInvalid(f, f == bad);
        }
    }

    private boolean fail(String message, JTextField field) {
        lblError.setText(message);
        markInvalid(field);
        if (field != null) {
            field.requestFocusInWindow();
        }
        return false;
    }

    private void saveProduct() {
        String name = txtProductName.getText().trim();
        if (name.isEmpty()) {
            fail("Please enter the product name.", txtProductName);
            return;
        }
        if (cmbCategory.getSelectedIndex() <= 0) {
            fail("Please select a category.", null);
            cmbCategory.requestFocusInWindow();
            return;
        }
        if (cmbUnit.getSelectedIndex() <= 0) {
            fail("Please select a unit.", null);
            cmbUnit.requestFocusInWindow();
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
        int stock;
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            fail("Opening stock must be a whole number.", txtStock);
            return;
        }
        if (stock < 0 || stock > 1_000_000) {
            fail("Opening stock must be between 0 and 1,000,000.", txtStock);
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
        markInvalid(null);
        lblError.setText(" ");

        Product p = new Product(0, name, cmbCategory.getSelectedItem().toString(),
                cmbUnit.getSelectedItem().toString(), selling, purchase, stock, -1);
        if (!ProductDAO.insert(p)) {
            fail("The product could not be saved. Please try again.", null);
            return;
        }
        JOptionPane.showMessageDialog(this, "Product \"" + name + "\" was added successfully.",
                "Product Saved", JOptionPane.INFORMATION_MESSAGE);
        if (onSaved != null) {
            onSaved.run();
        }
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
}
