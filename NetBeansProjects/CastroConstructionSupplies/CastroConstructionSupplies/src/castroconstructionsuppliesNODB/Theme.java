package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;

/** Castro Construction Supplies design tokens + small styling helpers shared by all screens. */
public final class Theme {

    public static final Color NAVY = new Color(0x0F172A);
    public static final Color STEEL = new Color(0x1E293B);
    public static final Color SOFT = new Color(0xF8FAFC);
    public static final Color ORANGE = new Color(0xF97316);
    public static final Color ORANGE_DARK = new Color(0xEA580C);
    public static final Color SUCCESS = new Color(0x22C55E);
    public static final Color WARNING = new Color(0xF59E0B);
    public static final Color DANGER = new Color(0xEF4444);
    public static final Color MUTED = new Color(0x94A3B8);
    public static final Color BORDER = new Color(0xE2E8F0);

    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADING = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font LABEL = new Font("Segoe UI", Font.BOLD, 12);

    private Theme() {
    }

    private static JButton button(String text, Color bg, Color bgHover, Color fg) {
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(bgHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    public static JButton primaryButton(String text) {
        return button(text, ORANGE, ORANGE_DARK, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return button(text, STEEL, new Color(0x334155), Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return button(text, DANGER, new Color(0xDC2626), Color.WHITE);
    }

    /** Text-only button that looks like a link (used for "Forgot password?"). */
    public static JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setUI(new BasicButtonUI());
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(ORANGE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static <T extends JTextField> T style(T field) {
        field.setFont(BODY);
        field.setBackground(Color.WHITE);
        field.setForeground(NAVY);
        field.setCaretColor(NAVY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        field.setPreferredSize(new Dimension(300, 36));
        return field;
    }

    public static <T> JComboBox<T> style(JComboBox<T> combo) {
        combo.setFont(BODY);
        combo.setPreferredSize(new Dimension(300, 36));
        return combo;
    }

    /** Small label above a field, light text for dark panels. */
    public static JPanel fieldBlock(String label, JComponent field) {
        return fieldBlock(label, field, new Color(0xCBD5E1));
    }

    public static JPanel fieldBlock(String label, JComponent field, Color labelColor) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(labelColor);
        l.setFont(LABEL);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    public static JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setForeground(new Color(0xFCA5A5));
        l.setFont(LABEL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static void styleTable(JTable t) {
        t.setRowHeight(32);
        t.setFont(BODY);
        t.setForeground(NAVY);
        t.setGridColor(BORDER);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(0xFFEDD5));
        t.setSelectionForeground(NAVY);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(NAVY);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(LABEL);
        t.getTableHeader().setPreferredSize(new Dimension(100, 36));
        DefaultTableCellRenderer header = new DefaultTableCellRenderer();
        header.setBackground(NAVY);
        header.setForeground(Color.WHITE);
        header.setFont(LABEL);
        header.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        t.getTableHeader().setDefaultRenderer(header);
    }

    public static final String PHONE_RULE = "Contact number must be 11 digits (e.g. 09171234567).";

    /** Philippine mobile format: exactly 11 digits starting with 09. */
    public static boolean isValidPhone(String s) {
        return s != null && s.trim().matches("09\\d{9}");
    }

    /** Lets a field accept digits only, max 11 characters. */
    public static void limitToPhone(JTextField field) {
        ((javax.swing.text.AbstractDocument) field.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                replace(fb, off, 0, str, a);
            }

            @Override
            public void replace(FilterBypass fb, int off, int len, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) {
                    str = "";
                }
                String digits = str.replaceAll("\\D", "");
                int room = 11 - (fb.getDocument().getLength() - len);
                if (room <= 0 && !digits.isEmpty()) {
                    return;
                }
                if (digits.length() > room) {
                    digits = digits.substring(0, Math.max(room, 0));
                }
                super.replace(fb, off, len, digits, a);
            }
        });
        field.setToolTipText("11-digit mobile number, e.g. 09171234567");
    }

    /** Red border on a field that failed validation (pass false to restore the normal border). */
    public static void setInvalid(JTextField field, boolean invalid) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(invalid ? DANGER : BORDER, invalid ? 2 : 1),
                BorderFactory.createEmptyBorder(invalid ? 5 : 6, invalid ? 9 : 10, invalid ? 5 : 6, invalid ? 9 : 10)));
    }

    /** Reads a money amount such as "1,250.50" or "P1250"; returns null when it is not a number. */
    public static Double parseMoney(String text) {
        try {
            String t = text.replace("\u20B1", "").replace(",", "").trim();
            if (t.isEmpty()) {
                return null;
            }
            double v = Double.parseDouble(t);
            return (Double.isNaN(v) || Double.isInfinite(v)) ? null : v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Shows a short error under a form instead of a popup. */
    public static void showError(JLabel label, String message) {
        label.setText(message);
    }
}
