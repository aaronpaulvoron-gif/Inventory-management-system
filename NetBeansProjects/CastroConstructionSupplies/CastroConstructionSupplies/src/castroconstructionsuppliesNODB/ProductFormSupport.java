package castroconstructionsuppliesNODB;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Pieces shared by the Add Product and Edit Product forms so both look and behave the same. */
final class ProductFormSupport {

    static final String[] UNITS = {"Select unit", "bag", "piece", "kg", "cubic meter", "sheet", "gallon", "roll", "set"};

    private ProductFormSupport() {
    }

    static JComboBox<String> categoryCombo() {
        JComboBox<String> c = new JComboBox<>();
        c.addItem("Select category");
        for (String name : CategoryDAO.findAllNames()) {
            c.addItem(name);
        }
        return Theme.style(c);
    }

    static JComboBox<String> unitCombo() {
        return Theme.style(new JComboBox<String>(UNITS));
    }

    /** Two fields side by side. */
    static JPanel pair(JComponent left, JComponent right) {
        JPanel r = new JPanel(new GridLayout(1, 2, 16, 0));
        r.setOpaque(false);
        r.add(left);
        r.add(right);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, r.getPreferredSize().height));
        return r;
    }

    static JLabel small(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.LABEL);
        l.setForeground(Theme.STEEL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /** Shows "Profit per unit" live while the user types the two prices. */
    static void bindProfit(JTextField purchase, JTextField selling, JLabel target) {
        DocumentListener l = new DocumentListener() {
            private void update() {
                Double cost = Theme.parseMoney(purchase.getText());
                Double sell = Theme.parseMoney(selling.getText());
                if (cost == null || sell == null || cost <= 0 || sell <= 0) {
                    target.setText(" ");
                    return;
                }
                double profit = sell - cost;
                target.setText(String.format("Profit per unit: \u20B1%,.2f  (%.1f%% of the purchase price)", profit, profit / cost * 100));
                target.setForeground(profit >= 0 ? new Color(30, 130, 76) : Theme.DANGER);
            }

            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        };
        purchase.getDocument().addDocumentListener(l);
        selling.getDocument().addDocumentListener(l);
        l.changedUpdate(null);
    }
}
