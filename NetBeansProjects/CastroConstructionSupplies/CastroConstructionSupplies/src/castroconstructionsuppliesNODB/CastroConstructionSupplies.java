package castroconstructionsuppliesNODB;

/**
 * Application entry point: opens the login screen.
 * <p>
 * This build keeps all data in memory for the current run only -- close
 * the program and everything entered is gone. The only account that
 * exists before you add anything is the built-in Staff login
 * "admin" / "admin123" (see Session.java). Everything else -- every
 * other staff account, every customer, every product, every sale -- is
 * created while the program runs and lives only in memory.
 * <p>
 * For data that survives a restart, see the other build of this project,
 * which saves everything to a MySQL database instead.
 */
public class CastroConstructionSupplies {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // fall back to the default look and feel
        }

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
