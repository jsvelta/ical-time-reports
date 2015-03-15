package cz.krasny.icalstats;

import cz.krasny.icalstats.gui.view.ViewMain;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/***
 * Main class
 * @author Tomas Krasny
 */
public class Main {
    
    /***
     * Main method.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    new ViewMain().setVisible(true);
                } catch (Exception ex) {
                    String err_text = "";
                    cz.krasny.icalstats.data.classes.Logger.appendException(ex);
                    err_text += ex.getMessage() + System.getProperty("line.separator") + System.getProperty("line.separator");
                    err_text += "See log.txt for more details.";
                    JOptionPane.showMessageDialog(null, err_text, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
