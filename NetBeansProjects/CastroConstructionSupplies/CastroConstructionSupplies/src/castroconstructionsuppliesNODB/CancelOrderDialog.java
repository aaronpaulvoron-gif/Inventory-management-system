package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * "Are you sure you want to cancel?" pop-up used by customers. It can also ask why
 * (the reason is saved with the cancelled order).
 */
public class CancelOrderDialog extends JDialog {

    public static final String[] REASONS = {
        "Changed my mind", "Ordered by mistake", "Found a better price",
        "Need to change the items", "Other"
    };

    /** Reasons the admin can pick when cancelling a customer's order. */
    public static final String[] STAFF_REASONS = {
        "Out of stock", "Product no longer available", "Wrong price listed",
        "Could not reach the customer", "Other"
    };

    private boolean confirmed;
    private final JComboBox<String> cmbReason;

    private CancelOrderDialog(Window owner, String heading, String message, boolean askReason,
            String[] reasons, String keepText, String cancelText) {
        super(owner, "Cancel", ModalityType.APPLICATION_MODAL);
        cmbReason = Theme.style(new JComboBox<String>(reasons));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.SOFT);

        JPanel band = new JPanel(new BorderLayout());
        band.setBackground(Theme.NAVY);
        band.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        JLabel title = new JLabel(heading);
        title.setFont(Theme.HEADING);
        title.setForeground(Theme.ORANGE);
        band.add(title, BorderLayout.WEST);
        root.add(band, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.SOFT);
        body.setBorder(BorderFactory.createEmptyBorder(20, 22, 8, 22));
        JLabel msg = new JLabel("<html><body style='width:340px'>" + message + "</body></html>");
        msg.setFont(Theme.BODY);
        msg.setForeground(Theme.NAVY);
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(msg);
        if (askReason) {
            body.add(Box.createVerticalStrut(16));
            cmbReason.setPreferredSize(new Dimension(340, 36));
            body.add(Theme.fieldBlock("Reason for cancelling", cmbReason, Theme.STEEL));
        }
        root.add(body, BorderLayout.CENTER);

        JButton btnKeep = Theme.secondaryButton(keepText);
        JButton btnCancel = Theme.dangerButton(cancelText);
        btnKeep.addActionListener(e -> dispose());
        btnCancel.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Theme.SOFT);
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 22, 18, 22));
        buttons.add(btnKeep);
        buttons.add(btnCancel);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(btnKeep);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /**
     * Shows the pop-up. Returns the chosen reason ("" when no reason was asked) if the
     * customer confirms the cancellation, or null when they keep their order.
     */
    public static String ask(Component parent, String heading, String message, boolean askReason,
            String keepText, String cancelText) {
        return ask(parent, heading, message, askReason, REASONS, keepText, cancelText);
    }

    /** Same, with your own list of reasons (staff use STAFF_REASONS). */
    public static String ask(Component parent, String heading, String message, boolean askReason,
            String[] reasons, String keepText, String cancelText) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        CancelOrderDialog d = new CancelOrderDialog(owner, heading, message, askReason, reasons, keepText, cancelText);
        d.setVisible(true);
        if (!d.confirmed) {
            return null;
        }
        return askReason ? String.valueOf(d.cmbReason.getSelectedItem()) : "";
    }
}
