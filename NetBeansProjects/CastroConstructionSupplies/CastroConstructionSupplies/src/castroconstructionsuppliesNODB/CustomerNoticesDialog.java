package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * "Order update" pop-up shown to a customer when staff cancelled one of their orders:
 * which order, and why. Each cancellation is shown once, then marked as seen.
 */
public class CustomerNoticesDialog extends JDialog {

    private CustomerNoticesDialog(Window owner, List<Sale> notices) {
        super(owner, "Order Update", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.SOFT);

        JPanel band = new JPanel(new BorderLayout());
        band.setBackground(Theme.NAVY);
        band.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        JLabel title = new JLabel(notices.size() == 1 ? "YOUR ORDER WAS CANCELLED" : "SOME OF YOUR ORDERS WERE CANCELLED");
        title.setFont(Theme.HEADING);
        title.setForeground(Theme.ORANGE);
        band.add(title, BorderLayout.WEST);
        root.add(band, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Theme.SOFT);
        list.setBorder(BorderFactory.createEmptyBorder(16, 22, 8, 22));
        for (Sale s : notices) {
            JPanel card = new JPanel(new BorderLayout(0, 4));
            card.setBackground(java.awt.Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.DANGER),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            JLabel head = new JLabel(String.format("%s   |   \u20B1%,.2f", s.getId(), s.getTotal()));
            head.setFont(Theme.HEADING);
            head.setForeground(Theme.NAVY);
            String why = s.getCancelReason().isEmpty() ? "No reason was given." : s.getCancelReason();
            JLabel body = new JLabel("<html><body style='width:320px'>Cancelled by the store.<br><b>Reason:</b> "
                    + why + "</body></html>");
            body.setFont(Theme.BODY);
            body.setForeground(Theme.STEEL);
            card.add(head, BorderLayout.NORTH);
            card.add(body, BorderLayout.CENTER);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            list.add(card);
            list.add(Box.createVerticalStrut(10));
        }
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.SOFT);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        int h = Math.min(list.getPreferredSize().height + 4, 300);
        scroll.setPreferredSize(new Dimension(Math.max(list.getPreferredSize().width, 400) + 20, h));
        root.add(scroll, BorderLayout.CENTER);

        JButton ok = Theme.primaryButton("OK, GOT IT");
        ok.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Theme.SOFT);
        buttons.setBorder(BorderFactory.createEmptyBorder(6, 22, 18, 22));
        buttons.add(ok);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(ok);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /** Shows the pop-up if staff cancelled any of this customer's orders since they last looked. */
    public static void showIfAny(Component parent) {
        if (!Session.isCustomer() || Session.currentCustomerId == null) {
            return;
        }
        List<Sale> notices = SaleDAO.findUnreadCancellations(Session.currentCustomerId);
        if (notices.isEmpty()) {
            return;
        }
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        if (owner == null && parent instanceof Window) {
            owner = (Window) parent;
        }
        new CustomerNoticesDialog(owner, notices).setVisible(true);
        SaleDAO.markNotified(Session.currentCustomerId);
    }
}
