package castroconstructionsuppliesNODB;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Color-codes a status word (Pending / Completed / Cancelled, or the
 * stock-level statuses used by Products and Inventory) so it's readable
 * at a glance across every table that shows one.
 */
public class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String status = String.valueOf(value);
        if (!isSelected) {
            switch (status) {
                case "OUT OF STOCK":
                case "Cancelled":
                    setForeground(new Color(190, 50, 45));
                    break;
                case "LOW STOCK":
                case "LIMITED STOCK":
                case "Pending":
                    setForeground(new Color(214, 120, 0));
                    break;
                case "IN STOCK":
                case "AVAILABLE":
                case "Completed":
                    setForeground(new Color(30, 130, 76));
                    break;
                default:
                    setForeground(Color.DARK_GRAY);
            }
        }
        setFont(getFont().deriveFont(Font.BOLD));
        return c;
    }
}
