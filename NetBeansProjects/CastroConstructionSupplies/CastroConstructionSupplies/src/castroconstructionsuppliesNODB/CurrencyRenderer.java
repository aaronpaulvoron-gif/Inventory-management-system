package castroconstructionsuppliesNODB;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Displays a numeric table cell (Double or Integer) as Philippine peso
 * currency, right-aligned. Because the underlying model value stays a
 * number (not a pre-formatted string), clicking the column header still
 * sorts by actual amount instead of alphabetically.
 */
public class CurrencyRenderer extends DefaultTableCellRenderer {

    public CurrencyRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        String text = (value instanceof Number)
                ? String.format("₱%,.2f", ((Number) value).doubleValue())
                : String.valueOf(value);
        java.awt.Component cell = super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        // a little padding so amounts never touch the next column
        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 14));
        return cell;
    }
}
