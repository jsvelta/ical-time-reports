package cz.krasny.icalstats.gui.view;

import cz.krasny.icalstats.data.classes.ICalRepresentation;
import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * View for managing ical representations.
 * @author Tomas Krasny
 */
public class ViewICalFileDetails extends javax.swing.JDialog {
    
    private ICalRepresentation icr = null;
    
    public ViewICalFileDetails(JFrame parent, boolean modal){
        this(null, parent, modal);
    }
    
    public ViewICalFileDetails(ICalRepresentation icrep, JFrame parent, boolean modal) {
        super(parent, modal);
        icr = icrep;
        initComponents();
        if(icrep != null){
            this.jTextField_name.setText(icrep.getName());
            if(icrep.getUrl() == null){
                jRadioButton_local_path.setSelected(true);
                jTextField_local_path.setText(icrep.getPath());
            }
            else {
                jRadioButton_URL.setSelected(true);
                jTextField_URL.setText(icrep.getUrl().toString());
            }
        }
        getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    public ICalRepresentation getICR(){
        return icr;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelMain = new javax.swing.JPanel();
        jRadioButton_local_path = new javax.swing.JRadioButton();
        jTextField_local_path = new javax.swing.JTextField();
        jTextField_URL = new javax.swing.JTextField();
        jRadioButton_URL = new javax.swing.JRadioButton();
        jLabelName = new javax.swing.JLabel();
        jTextField_name = new javax.swing.JTextField();
        jPanelButtons = new javax.swing.JPanel();
        jButton_cancel = new javax.swing.JButton();
        jButton_OK = new javax.swing.JButton();
        jButtonSelect = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("iCal file representation");
        setPreferredSize(new java.awt.Dimension(615, 170));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("ICalFile details"));
        jPanelMain.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelMain.setPreferredSize(new java.awt.Dimension(615, 170));
        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jRadioButton_local_path.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(jRadioButton_local_path);
        jRadioButton_local_path.setSelected(true);
        jRadioButton_local_path.setText("local path:");
        jRadioButton_local_path.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton_local_pathItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        jPanelMain.add(jRadioButton_local_path, gridBagConstraints);
        jRadioButton_local_path.getAccessibleContext().setAccessibleDescription("");

        jTextField_local_path.setEditable(false);
        jTextField_local_path.setBackground(new java.awt.Color(255, 255, 255));
        jTextField_local_path.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.weightx = 1.0;
        jPanelMain.add(jTextField_local_path, gridBagConstraints);

        jTextField_URL.setToolTipText("");
        jTextField_URL.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 1);
        jPanelMain.add(jTextField_URL, gridBagConstraints);

        jRadioButton_URL.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(jRadioButton_URL);
        jRadioButton_URL.setText("URL:");
        jRadioButton_URL.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton_local_pathItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanelMain.add(jRadioButton_URL, gridBagConstraints);

        jLabelName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMain.add(jLabelName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 1);
        jPanelMain.add(jTextField_name, gridBagConstraints);

        jPanelButtons.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButton_cancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritecancel.png"))); // NOI18N
        jButton_cancel.setText("Cancel");
        jButton_cancel.setMaximumSize(new java.awt.Dimension(100, 25));
        jButton_cancel.setMinimumSize(new java.awt.Dimension(100, 25));
        jButton_cancel.setPreferredSize(new java.awt.Dimension(100, 25));
        jButton_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanelButtons.add(jButton_cancel, gridBagConstraints);

        jButton_OK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spriteok.png"))); // NOI18N
        jButton_OK.setText("OK");
        jButton_OK.setMaximumSize(new java.awt.Dimension(100, 25));
        jButton_OK.setMinimumSize(new java.awt.Dimension(100, 25));
        jButton_OK.setPreferredSize(new java.awt.Dimension(100, 25));
        jButton_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_OKActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanelButtons.add(jButton_OK, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
        jPanelMain.add(jPanelButtons, gridBagConstraints);

        jButtonSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spriteopen.png"))); // NOI18N
        jButtonSelect.setText("Browse...");
        jButtonSelect.setMaximumSize(new java.awt.Dimension(100, 22));
        jButtonSelect.setMinimumSize(new java.awt.Dimension(100, 22));
        jButtonSelect.setPreferredSize(new java.awt.Dimension(100, 22));
        jButtonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanelMain.add(jButtonSelect, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 1);
        jPanelMain.add(jSeparator3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelMain, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_jButton_cancelActionPerformed

    private void jButton_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_OKActionPerformed
        String name = this.jTextField_name.getText();
        String source = "";
        if(icr == null)
            icr = new ICalRepresentation();
        if(name.trim().equals("")){
            JOptionPane.showMessageDialog(this, "Name can not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.icr.setName(name.trim());
        if(this.jRadioButton_URL.isSelected()){
            source = this.jTextField_URL.getText();
            this.icr.setPath(null);
            try {
                this.icr.setUrl(new URL(source));
                dispose();
            } catch (MalformedURLException ex) {
                this.icr = null;
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else{
            source = this.jTextField_local_path.getText();
            this.icr.setPath(source);
            this.icr.setUrl(null);
            if(source.trim().equals("")){
                JOptionPane.showMessageDialog(this, "Path can not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                dispose();
            }
        }

    }//GEN-LAST:event_jButton_OKActionPerformed

    private void jRadioButton_local_pathItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton_local_pathItemStateChanged
        if(this.jRadioButton_URL.isSelected()){
            this.jTextField_URL.setEnabled(true);
        }
        else{
            this.jTextField_URL.setEnabled(false);
        }
        if(this.jRadioButton_local_path.isSelected()){
            this.jTextField_local_path.setEnabled(true);
            this.jButtonSelect.setEnabled(true);
        }
        else{
            this.jTextField_local_path.setEnabled(false);
            this.jButtonSelect.setEnabled(false);
        }
    }//GEN-LAST:event_jRadioButton_local_pathItemStateChanged

    private void jButtonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectActionPerformed
        File file = null;
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("iCal files (*.ics)", "ics");
        jfc.setFileFilter(filter);
        jfc.setAcceptAllFileFilterUsed(false);
        if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            file = jfc.getSelectedFile();
            if(!file.exists())
                JOptionPane.showMessageDialog(null, "Directory does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            else
                this.jTextField_local_path.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_jButtonSelectActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonSelect;
    private javax.swing.JButton jButton_OK;
    private javax.swing.JButton jButton_cancel;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JRadioButton jRadioButton_URL;
    private javax.swing.JRadioButton jRadioButton_local_path;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextField_URL;
    private javax.swing.JTextField jTextField_local_path;
    private javax.swing.JTextField jTextField_name;
    // End of variables declaration//GEN-END:variables
}
