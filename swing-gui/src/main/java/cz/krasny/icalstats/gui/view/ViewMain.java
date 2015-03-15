package cz.krasny.icalstats.gui.view;

import cz.krasny.icalstats.core.managers.FileManager;
import cz.krasny.icalstats.core.managers.KeywordsManager;
import cz.krasny.icalstats.core.managers.XMLManager;
import cz.krasny.icalstats.data.classes.DateRange;
import cz.krasny.icalstats.data.classes.output.ExportConfiguration;
import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.ICalRepresentation;
import cz.krasny.icalstats.data.classes.Keyword;
import cz.krasny.icalstats.data.classes.backgroundworkers.ExportBackgroundWorker;
import cz.krasny.icalstats.data.classes.backgroundworkers.KeywordBackgroundWorker;
import cz.krasny.icalstats.data.classes.models.CalendarsComboBoxModel;
import cz.krasny.icalstats.data.classes.models.ICalRepresentationTableModel;
import cz.krasny.icalstats.data.classes.models.KeywordsTableModel;
import cz.krasny.icalstats.data.classes.output.GroupBy;
import cz.krasny.icalstats.data.classes.output.OutputFormat;
import cz.krasny.icalstats.data.classes.output.Units;
import cz.krasny.icalstats.data.classes.renderers.CalendarsComboBoxCellRenderer;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_FOCUSED;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.joda.time.format.DateTimeFormat;

/***
 * This class is the main window of this application.
 * Represents the main GUI.
 * @author Tomas Krasny
 */
public class ViewMain extends javax.swing.JFrame {
    
    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private KeywordsTableModel model_kw = null;
    private ICalRepresentationTableModel model_icr = null;
    private CalendarsComboBoxModel model_calendars = null;
    
    public ViewMain() throws MalformedURLException {
        initComponents();
        
        initICalRepresentationTable();
        initKeywordTables();
        initComboboxes();
        initComponentsOther();
        
        addData();
    }
    
    private void initICalRepresentationTable(){
        /* Column names and model */
        ArrayList<String> col_names = new ArrayList<>();
        col_names.add("Selected");
        col_names.add("Name");
        col_names.add("Source");
        model_icr = new ICalRepresentationTableModel(new ArrayList<ICalRepresentation>(), col_names);
        jTableICalRepresentation.setModel(model_icr);
        
        /* set column widths */
        TableColumnModel tcm = jTableICalRepresentation.getColumnModel();
        tcm.getColumn(0).setPreferredWidth((int) (0.075 * 10000));
        tcm.getColumn(1).setPreferredWidth((int) (0.200 * 10000));
        tcm.getColumn(2).setPreferredWidth((int) (0.725 * 10000));
        
        model_icr.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                jTableICalRepresentation_valueChanged();
            } 
        }); 
        
        /* Add DELETE listener */
        InputMap im = jTableICalRepresentation.getInputMap(WHEN_FOCUSED);
        ActionMap am = jTableICalRepresentation.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        am.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
               jButtonDelete.doClick();
            }
        });
    }
    private void initKeywordTables(){
        /* Column names and model */
        ArrayList<String> col_names = new ArrayList<>();
        col_names = new ArrayList<>();
        col_names.add("Keyword");
        col_names.add("Occurrence");
        model_kw = new KeywordsTableModel(new ArrayList<Keyword>(), col_names);
        jTableKeywords.setModel(model_kw);
        jTableKeywordsExport.setModel(model_kw);
        
        /* Left align to second column */
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        jTableKeywords.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        jTableKeywordsExport.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        
        /* model changed */
        model_kw.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                jLabelKeywordsCountValue.setText(model_kw.getKeywords().size() + "");
            }
        }); 
        
        /* add delete key */
        InputMap im = jTableKeywords.getInputMap(WHEN_FOCUSED);
        ActionMap am = jTableKeywords.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        am.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
               jButtonDeleteKeywords.doClick();
            }
        });        
    }
    private void initComboboxes(){
        model_calendars = new CalendarsComboBoxModel(model_icr.getICRData());
        CalendarsComboBoxCellRenderer renderrer = new CalendarsComboBoxCellRenderer();
        jComboBoxCalendars.setModel(model_calendars);
        jComboBoxCalendars.setRenderer(renderrer);
        
        jComboBoxGranularity.setModel(new DefaultComboBoxModel(GroupBy.values()));
        jComboBoxGranularity.setSelectedIndex(0);
        jComboBoxUnits.setModel(new DefaultComboBoxModel(Units.values()));
        jComboBoxUnits.setSelectedIndex(1);
    }
    private void initComponentsOther(){
        /* set tabs sizes */
        jTabbedPaneMain.setTitleAt(0, "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>Calendars</body></html>");
        jTabbedPaneMain.setTitleAt(1, "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>Keywords</body></html>");
        jTabbedPaneMain.setTitleAt(2, "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>Statistics</body></html>");
        jTabbedPaneMain.setTitleAt(3, "<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>Export</body></html>");
        
        /* other */
        jSpinnerKeywords.setEditor(new JSpinner.DefaultEditor(jSpinnerKeywords));
        jRadioButtonWholeCalendar.setSelected(true);
        jRadioButtonWholeCalendarExport.setSelected(true);
        jRadioButtonXlsx.setSelected(true);
        
        setLocationRelativeTo(null);
    }
    
    private void addData(){
        XMLManager xml = XMLManager.getInstance();
        String err_text = "";
        try {
            model_icr.addAll(xml.loadICalRepresentations());            
        } catch (Exception ex) {
            cz.krasny.icalstats.data.classes.Logger.appendException(ex);
            err_text += ex.getMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
            err_text += "See log.txt for more details.";
            JOptionPane.showMessageDialog(this, err_text, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup_format = new javax.swing.ButtonGroup();
        jPanel_MAIN = new javax.swing.JPanel();
        jPanel_bottom_end = new javax.swing.JPanel();
        jButton_exit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel_selected_file = new javax.swing.JPanel();
        jLabel_selectedText = new javax.swing.JLabel();
        jLabel_name = new javax.swing.JLabel();
        jLabel_source = new javax.swing.JLabel();
        jLabel_source_real = new javax.swing.JLabel();
        jLabel_name_real = new javax.swing.JLabel();
        buttonGroup_source = new javax.swing.ButtonGroup();
        buttonGroup_dateKeywords = new javax.swing.ButtonGroup();
        buttonGroup_datesExport = new javax.swing.ButtonGroup();
        buttonGroupGranularity = new javax.swing.ButtonGroup();
        buttonGroupUnits = new javax.swing.ButtonGroup();
        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jPanelCalendarRepresentations = new javax.swing.JPanel();
        jScrollPaneICalRepresentation = new javax.swing.JScrollPane();
        jTableICalRepresentation = new javax.swing.JTable();
        jPanelButtons = new javax.swing.JPanel();
        jButtonAdd = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanelKeywords = new javax.swing.JPanel();
        jButtonParse = new javax.swing.JButton();
        jPanelOptions = new javax.swing.JPanel();
        jDateChooserDateTo = new com.toedter.calendar.JDateChooser();
        jRadioButtonWholeCalendar = new javax.swing.JRadioButton();
        jSpinnerKeywords = new javax.swing.JSpinner();
        jRadioButtonDateFrom = new javax.swing.JRadioButton();
        jComboBoxCalendars = new javax.swing.JComboBox();
        jLabelDateTo = new javax.swing.JLabel();
        jDateChooserDateFrom = new com.toedter.calendar.JDateChooser();
        jRadioButtonFromCalendar = new javax.swing.JRadioButton();
        jLabelMinimalOccurrence = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0), new java.awt.Dimension(100, 0), new java.awt.Dimension(100, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(120, 0), new java.awt.Dimension(120, 0), new java.awt.Dimension(120, 32767));
        jTextFieldRegex = new javax.swing.JTextField();
        jLabelRegex = new javax.swing.JLabel();
        jButtonBrowse = new javax.swing.JButton();
        jRadioButtonFromFile = new javax.swing.JRadioButton();
        jCheckBoxCaseSensitive = new javax.swing.JCheckBox();
        jCheckBoxRemoveDiacritics = new javax.swing.JCheckBox();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(70, 0), new java.awt.Dimension(70, 0), new java.awt.Dimension(70, 32767));
        jScrollPaneKeywords = new javax.swing.JScrollPane();
        jTableKeywords = new javax.swing.JTable();
        jPanelOtherKeywords = new javax.swing.JPanel();
        jButtonAddOtherKeywords = new javax.swing.JButton();
        jLabelOtherKeywords = new javax.swing.JLabel();
        jScrollPaneOtherKeywords = new javax.swing.JScrollPane();
        jTextAreaOtherKeywords = new javax.swing.JTextArea();
        jPanelButtonsKeywords = new javax.swing.JPanel();
        jButtonSaveKeywords = new javax.swing.JButton();
        jButtonDeleteKeywords = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel2 = new javax.swing.JPanel();
        jLabelKeywordsCount = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabelKeywordsCountValue = new javax.swing.JLabel();
        jPanelStatistics = new javax.swing.JPanel();
        jLabelGranularity = new javax.swing.JLabel();
        jLabelUnits = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 5), new java.awt.Dimension(5, 5), new java.awt.Dimension(0, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 5), new java.awt.Dimension(5, 5), new java.awt.Dimension(32767, 0));
        jComboBoxGranularity = new javax.swing.JComboBox();
        jComboBoxUnits = new javax.swing.JComboBox();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(70, 0), new java.awt.Dimension(70, 0), new java.awt.Dimension(70, 32767));
        jRadioButtonXls = new javax.swing.JRadioButton();
        jRadioButtonHtml = new javax.swing.JRadioButton();
        jLabelFormatExport = new javax.swing.JLabel();
        jRadioButtonXlsx = new javax.swing.JRadioButton();
        jRadioButtonDateFromExport = new javax.swing.JRadioButton();
        jLabel_dateToExport = new javax.swing.JLabel();
        jDateChooserDateToExport = new com.toedter.calendar.JDateChooser();
        jDateChooserDateFromExport = new com.toedter.calendar.JDateChooser();
        jRadioButtonWholeCalendarExport = new javax.swing.JRadioButton();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jCheckBoxIncludeEvents = new javax.swing.JCheckBox();
        jCheckBoxIncludeFromTo = new javax.swing.JCheckBox();
        jCheckBoxIncludeDays = new javax.swing.JCheckBox();
        jCheckBoxIncludeDate = new javax.swing.JCheckBox();
        jCheckBoxIncludeEmptyRows = new javax.swing.JCheckBox();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0), new java.awt.Dimension(100, 0), new java.awt.Dimension(150, 32767));
        jPanelExport = new javax.swing.JPanel();
        jPanelSummary = new javax.swing.JPanel();
        jPanelSelectedKeywordsExport = new javax.swing.JPanel();
        jScrollPaneSelectedKeywordsExport = new javax.swing.JScrollPane();
        jTableKeywordsExport = new javax.swing.JTable();
        jLabelKeywords = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabelInclDaysSummary = new javax.swing.JLabel();
        jLabelUnitsSummary = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jScrollPaneSelectedCalendars = new javax.swing.JScrollPane();
        jTextAreaSelectedCalendars = new javax.swing.JTextArea();
        jLabelInclFromToValue = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabelDateRangeValue = new javax.swing.JLabel();
        jLabelInclEmptyRowsSummary = new javax.swing.JLabel();
        jLabelInclOutputFSummary = new javax.swing.JLabel();
        jLabelDateRangeSummary = new javax.swing.JLabel();
        jLabelInclFromToSummary = new javax.swing.JLabel();
        jLabelGranularityValue = new javax.swing.JLabel();
        jLabelRemoveDiacriticsValue = new javax.swing.JLabel();
        jLabelInclDatesSummary = new javax.swing.JLabel();
        jLabelInclEventsValue = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabelCaseSensitive = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabelInclEmptyRValue = new javax.swing.JLabel();
        jLabelInclEventsSummary = new javax.swing.JLabel();
        jLabelInclDatesValue = new javax.swing.JLabel();
        jLabelSelectedCalendar = new javax.swing.JLabel();
        jLabelOutputFValue = new javax.swing.JLabel();
        jLabelGranularitySummary = new javax.swing.JLabel();
        jLabelInclDaysValue = new javax.swing.JLabel();
        jLabelCaseSensitiveValue = new javax.swing.JLabel();
        jLabelUnitsValue = new javax.swing.JLabel();
        jLabelRemoveDiacritics = new javax.swing.JLabel();
        jPanelExportOptions = new javax.swing.JPanel();
        jCheckBoxSave = new javax.swing.JCheckBox();
        jLabelOption = new javax.swing.JLabel();
        jCheckBoxOpen = new javax.swing.JCheckBox();
        jButtonExport = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        jPanel_MAIN.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_MAIN.setMinimumSize(new java.awt.Dimension(768, 585));
        jPanel_MAIN.setName(""); // NOI18N

        jPanel_bottom_end.setBackground(new java.awt.Color(204, 204, 204));
        jPanel_bottom_end.setName(""); // NOI18N

        jButton_exit.setText("Exit");
        jButton_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_exitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_bottom_endLayout = new javax.swing.GroupLayout(jPanel_bottom_end);
        jPanel_bottom_end.setLayout(jPanel_bottom_endLayout);
        jPanel_bottom_endLayout.setHorizontalGroup(
            jPanel_bottom_endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_bottom_endLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_bottom_endLayout.setVerticalGroup(
            jPanel_bottom_endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bottom_endLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton_exit)
                .addContainerGap())
        );

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        jPanel_selected_file.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_selected_file.setFocusable(false);

        jLabel_selectedText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel_selectedText.setText("Selected file:");

        jLabel_name.setText("Name:");

        jLabel_source.setText("Source:");

        jLabel_source_real.setBackground(new java.awt.Color(153, 153, 255));
        jLabel_source_real.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel_source_real.setText("      ");
        jLabel_source_real.setToolTipText("");

        jLabel_name_real.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel_name_real.setText("      ");

        javax.swing.GroupLayout jPanel_selected_fileLayout = new javax.swing.GroupLayout(jPanel_selected_file);
        jPanel_selected_file.setLayout(jPanel_selected_fileLayout);
        jPanel_selected_fileLayout.setHorizontalGroup(
            jPanel_selected_fileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_selected_fileLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_selected_fileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_selectedText)
                    .addGroup(jPanel_selected_fileLayout.createSequentialGroup()
                        .addGroup(jPanel_selected_fileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_name)
                            .addComponent(jLabel_source))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_selected_fileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_name_real, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel_source_real, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel_selected_fileLayout.setVerticalGroup(
            jPanel_selected_fileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_selected_fileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_selectedText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_selected_fileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_name)
                    .addComponent(jLabel_name_real))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_selected_fileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_source)
                    .addComponent(jLabel_source_real))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel_selected_file);

        javax.swing.GroupLayout jPanel_MAINLayout = new javax.swing.GroupLayout(jPanel_MAIN);
        jPanel_MAIN.setLayout(jPanel_MAINLayout);
        jPanel_MAINLayout.setHorizontalGroup(
            jPanel_MAINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_MAINLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_MAINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel_bottom_end, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_MAINLayout.setVerticalGroup(
            jPanel_MAINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_MAINLayout.createSequentialGroup()
                .addGap(420, 420, 420)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_bottom_end, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("iCal statistics");
        setBackground(new java.awt.Color(255, 255, 255));
        setName("mainWindow"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jTabbedPaneMain.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPaneMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTabbedPaneMain.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPaneMain.setPreferredSize(new java.awt.Dimension(757, 342));

        jPanelCalendarRepresentations.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCalendarRepresentations.setBorder(javax.swing.BorderFactory.createTitledBorder("Calendar settings"));
        jPanelCalendarRepresentations.setPreferredSize(new java.awt.Dimension(757, 342));
        jPanelCalendarRepresentations.setLayout(new java.awt.GridBagLayout());

        jScrollPaneICalRepresentation.setBackground(new java.awt.Color(255, 255, 255));

        jTableICalRepresentation.setAutoCreateRowSorter(true);
        jTableICalRepresentation.setModel(jTableICalRepresentation.getModel());
        jTableICalRepresentation.setRowHeight(20);
        jTableICalRepresentation.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTableICalRepresentation.getTableHeader().setReorderingAllowed(false);
        jTableICalRepresentation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableICalRepresentationMouseClicked(evt);
            }
        });
        jScrollPaneICalRepresentation.setViewportView(jTableICalRepresentation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelCalendarRepresentations.add(jScrollPaneICalRepresentation, gridBagConstraints);

        jPanelButtons.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritesnew.png"))); // NOI18N
        jButtonAdd.setText("New");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelButtons.add(jButtonAdd, gridBagConstraints);

        jButtonEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritesedit.png"))); // NOI18N
        jButtonEdit.setText("Edit");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelButtons.add(jButtonEdit, gridBagConstraints);

        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritesdelete.png"))); // NOI18N
        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelButtons.add(jButtonDelete, gridBagConstraints);

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritessave.png"))); // NOI18N
        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelButtons.add(jButtonSave, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelButtons.add(filler6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        jPanelCalendarRepresentations.add(jPanelButtons, gridBagConstraints);

        jTabbedPaneMain.addTab("Calendars", jPanelCalendarRepresentations);

        jPanelKeywords.setBackground(new java.awt.Color(255, 255, 255));
        jPanelKeywords.setBorder(javax.swing.BorderFactory.createTitledBorder("Keyword settings"));
        jPanelKeywords.setPreferredSize(new java.awt.Dimension(757, 342));
        jPanelKeywords.setLayout(new java.awt.GridBagLayout());

        jButtonParse.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonParse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritesexport.png"))); // NOI18N
        jButtonParse.setText("PARSE");
        jButtonParse.setEnabled(false);
        jButtonParse.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonParse.setMaximumSize(new java.awt.Dimension(121, 25));
        jButtonParse.setMinimumSize(new java.awt.Dimension(121, 25));
        jButtonParse.setPreferredSize(new java.awt.Dimension(121, 25));
        jButtonParse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonParseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        jPanelKeywords.add(jButtonParse, gridBagConstraints);

        jPanelOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPanelOptions.setLayout(new java.awt.GridBagLayout());

        jDateChooserDateTo.setDateFormatString("dd.MM.yyyy");
        jDateChooserDateTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserDateFromPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 1.0;
        jPanelOptions.add(jDateChooserDateTo, gridBagConstraints);

        jRadioButtonWholeCalendar.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_dateKeywords.add(jRadioButtonWholeCalendar);
        jRadioButtonWholeCalendar.setSelected(true);
        jRadioButtonWholeCalendar.setText("Whole calendar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        jPanelOptions.add(jRadioButtonWholeCalendar, gridBagConstraints);

        jSpinnerKeywords.setModel(new javax.swing.SpinnerNumberModel(5, 1, 10000, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelOptions.add(jSpinnerKeywords, gridBagConstraints);

        jRadioButtonDateFrom.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_dateKeywords.add(jRadioButtonDateFrom);
        jRadioButtonDateFrom.setText("Date from:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelOptions.add(jRadioButtonDateFrom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelOptions.add(jComboBoxCalendars, gridBagConstraints);
        jComboBoxCalendars.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int index = jComboBoxCalendars.getSelectedIndex();
                if(index > -1){
                    jRadioButtonFromCalendar.setSelected(true);
                    jButtonParse.setEnabled(true);
                }
                else{
                    jButtonParse.setEnabled(false);
                }
            }
        });

        jLabelDateTo.setText("Date to:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 21, 0, 0);
        jPanelOptions.add(jLabelDateTo, gridBagConstraints);

        jDateChooserDateFrom.setDateFormatString("dd.MM.yyyy");
        jDateChooserDateFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserDateFromPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 1.0;
        jPanelOptions.add(jDateChooserDateFrom, gridBagConstraints);

        jRadioButtonFromCalendar.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_source.add(jRadioButtonFromCalendar);
        jRadioButtonFromCalendar.setText("From calendar:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelOptions.add(jRadioButtonFromCalendar, gridBagConstraints);
        jRadioButtonFromCalendar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(jRadioButtonFromCalendar.isSelected()){
                    if(jComboBoxCalendars.getSelectedIndex() > -1){
                        jButtonParse.setEnabled(true);
                    }
                    else{
                        jButtonParse.setEnabled(false);
                    }
                }
                else{
                    jButtonParse.setEnabled(false);
                }
            }
        });

        jLabelMinimalOccurrence.setText("Minimal occurrence:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelOptions.add(jLabelMinimalOccurrence, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelOptions.add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        jPanelOptions.add(filler4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        jPanelOptions.add(filler3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelOptions.add(jTextFieldRegex, gridBagConstraints);

        jLabelRegex.setText("Matches regexp:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanelOptions.add(jLabelRegex, gridBagConstraints);

        jButtonBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spriteopen.png"))); // NOI18N
        jButtonBrowse.setText("Browse...");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanelOptions.add(jButtonBrowse, gridBagConstraints);

        jRadioButtonFromFile.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_source.add(jRadioButtonFromFile);
        jRadioButtonFromFile.setSelected(true);
        jRadioButtonFromFile.setText("From file:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelOptions.add(jRadioButtonFromFile, gridBagConstraints);
        jRadioButtonFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(jRadioButtonFromFile.isSelected()){
                    jButtonParse.setEnabled(false);
                }
                else{
                    jButtonParse.setEnabled(true);
                }
            }
        });

        jCheckBoxCaseSensitive.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxCaseSensitive.setText("Case sensitive");
        jCheckBoxCaseSensitive.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxesIncludingStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        jPanelOptions.add(jCheckBoxCaseSensitive, gridBagConstraints);

        jCheckBoxRemoveDiacritics.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxRemoveDiacritics.setText("Remove diacritics");
        jCheckBoxRemoveDiacritics.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxesIncludingStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        jPanelOptions.add(jCheckBoxRemoveDiacritics, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelOptions.add(filler11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelKeywords.add(jPanelOptions, gridBagConstraints);

        jScrollPaneKeywords.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTableKeywords.setAutoCreateRowSorter(true);
        jTableKeywords.setRowHeight(20);
        jTableKeywords.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTableKeywords.getTableHeader().setReorderingAllowed(false);
        jScrollPaneKeywords.setViewportView(jTableKeywords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 4);
        jPanelKeywords.add(jScrollPaneKeywords, gridBagConstraints);

        jPanelOtherKeywords.setBackground(new java.awt.Color(255, 255, 255));
        jPanelOtherKeywords.setLayout(new java.awt.GridBagLayout());

        jButtonAddOtherKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritesnew.png"))); // NOI18N
        jButtonAddOtherKeywords.setText("Add");
        jButtonAddOtherKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddOtherKeywordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelOtherKeywords.add(jButtonAddOtherKeywords, gridBagConstraints);

        jLabelOtherKeywords.setText("Other keywods (separated by semicolon):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelOtherKeywords.add(jLabelOtherKeywords, gridBagConstraints);

        jTextAreaOtherKeywords.setColumns(20);
        jTextAreaOtherKeywords.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextAreaOtherKeywords.setRows(3);
        jTextAreaOtherKeywords.setMinimumSize(new java.awt.Dimension(4, 30));
        jScrollPaneOtherKeywords.setViewportView(jTextAreaOtherKeywords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.55;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 1);
        jPanelOtherKeywords.add(jScrollPaneOtherKeywords, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.55;
        gridBagConstraints.weighty = 1.0;
        jPanelKeywords.add(jPanelOtherKeywords, gridBagConstraints);

        jPanelButtonsKeywords.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonsKeywords.setLayout(new java.awt.GridBagLayout());

        jButtonSaveKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritessave.png"))); // NOI18N
        jButtonSaveKeywords.setText("Save");
        jButtonSaveKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveKeywordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanelButtonsKeywords.add(jButtonSaveKeywords, gridBagConstraints);

        jButtonDeleteKeywords.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritesdelete.png"))); // NOI18N
        jButtonDeleteKeywords.setText("Delete");
        jButtonDeleteKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteKeywordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 5, 0);
        jPanelButtonsKeywords.add(jButtonDeleteKeywords, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelButtonsKeywords.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 3);
        jPanelKeywords.add(jPanelButtonsKeywords, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabelKeywordsCount.setText("Keywords count: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel2.add(jLabelKeywordsCount, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(filler10, gridBagConstraints);

        jLabelKeywordsCountValue.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel2.add(jLabelKeywordsCountValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelKeywords.add(jPanel2, gridBagConstraints);

        jTabbedPaneMain.addTab("Keywords", jPanelKeywords);

        jPanelStatistics.setBackground(new java.awt.Color(255, 255, 255));
        jPanelStatistics.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistic settings"));
        jPanelStatistics.setPreferredSize(new java.awt.Dimension(757, 342));
        jPanelStatistics.setLayout(new java.awt.GridBagLayout());

        jLabelGranularity.setText("Group by:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanelStatistics.add(jLabelGranularity, gridBagConstraints);

        jLabelUnits.setText("Units:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelStatistics.add(jLabelUnits, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelStatistics.add(filler7, gridBagConstraints);

        filler8.setBackground(new java.awt.Color(51, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelStatistics.add(filler8, gridBagConstraints);

        jComboBoxGranularity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxGranularity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxGranularityActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelStatistics.add(jComboBoxGranularity, gridBagConstraints);

        jComboBoxUnits.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUnitsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelStatistics.add(jComboBoxUnits, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        jPanelStatistics.add(filler9, gridBagConstraints);

        jRadioButtonXls.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_format.add(jRadioButtonXls);
        jRadioButtonXls.setText("XLS");
        jRadioButtonXls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtons_outputFormatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelStatistics.add(jRadioButtonXls, gridBagConstraints);

        jRadioButtonHtml.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_format.add(jRadioButtonHtml);
        jRadioButtonHtml.setText("HTML");
        jRadioButtonHtml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtons_outputFormatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelStatistics.add(jRadioButtonHtml, gridBagConstraints);

        jLabelFormatExport.setText("Output format:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelStatistics.add(jLabelFormatExport, gridBagConstraints);

        jRadioButtonXlsx.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_format.add(jRadioButtonXlsx);
        jRadioButtonXlsx.setSelected(true);
        jRadioButtonXlsx.setText("XLSX");
        jRadioButtonXlsx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtons_outputFormatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelStatistics.add(jRadioButtonXlsx, gridBagConstraints);

        jRadioButtonDateFromExport.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_datesExport.add(jRadioButtonDateFromExport);
        jRadioButtonDateFromExport.setText("Date from:");
        jRadioButtonDateFromExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtons_dateRange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelStatistics.add(jRadioButtonDateFromExport, gridBagConstraints);

        jLabel_dateToExport.setText("Date to:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 26, 0, 0);
        jPanelStatistics.add(jLabel_dateToExport, gridBagConstraints);

        jDateChooserDateToExport.setDateFormatString("dd.MM.yyyy");
        jDateChooserDateToExport.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserDatesExportPropertyChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelStatistics.add(jDateChooserDateToExport, gridBagConstraints);

        jDateChooserDateFromExport.setDateFormatString("dd.MM.yyyy");
        jDateChooserDateFromExport.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserDatesExportPropertyChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelStatistics.add(jDateChooserDateFromExport, gridBagConstraints);

        jRadioButtonWholeCalendarExport.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_datesExport.add(jRadioButtonWholeCalendarExport);
        jRadioButtonWholeCalendarExport.setSelected(true);
        jRadioButtonWholeCalendarExport.setText("Whole calendar");
        jRadioButtonWholeCalendarExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtons_dateRange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanelStatistics.add(jRadioButtonWholeCalendarExport, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelStatistics.add(jSeparator2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 5, 0);
        jPanelStatistics.add(jSeparator3, gridBagConstraints);

        jCheckBoxIncludeEvents.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxIncludeEvents.setText("Include events");
        jCheckBoxIncludeEvents.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxesIncludingStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelStatistics.add(jCheckBoxIncludeEvents, gridBagConstraints);

        jCheckBoxIncludeFromTo.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxIncludeFromTo.setText("Include from-to");
        jCheckBoxIncludeFromTo.setEnabled(false);
        jCheckBoxIncludeFromTo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxesIncludingStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        jPanelStatistics.add(jCheckBoxIncludeFromTo, gridBagConstraints);

        jCheckBoxIncludeDays.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxIncludeDays.setText("Include Day of Week names");
        jCheckBoxIncludeDays.setEnabled(false);
        jCheckBoxIncludeDays.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxesIncludingStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        jPanelStatistics.add(jCheckBoxIncludeDays, gridBagConstraints);

        jCheckBoxIncludeDate.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxIncludeDate.setText("Include dates");
        jCheckBoxIncludeDate.setEnabled(false);
        jCheckBoxIncludeDate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxesIncludingStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        jPanelStatistics.add(jCheckBoxIncludeDate, gridBagConstraints);

        jCheckBoxIncludeEmptyRows.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxIncludeEmptyRows.setText("Include empty rows");
        jCheckBoxIncludeEmptyRows.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxesIncludingStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelStatistics.add(jCheckBoxIncludeEmptyRows, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanelStatistics.add(filler5, gridBagConstraints);

        jTabbedPaneMain.addTab("Statistics", jPanelStatistics);

        jPanelExport.setBackground(new java.awt.Color(255, 255, 255));
        jPanelExport.setPreferredSize(new java.awt.Dimension(757, 342));
        jPanelExport.setLayout(new java.awt.GridBagLayout());

        jPanelSummary.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSummary.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistic summary"));
        jPanelSummary.setLayout(new java.awt.GridBagLayout());

        jPanelSelectedKeywordsExport.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSelectedKeywordsExport.setLayout(new java.awt.GridBagLayout());

        jTableKeywordsExport.setAutoCreateRowSorter(true);
        jTableKeywordsExport.setRowHeight(20);
        jTableKeywordsExport.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneSelectedKeywordsExport.setViewportView(jTableKeywordsExport);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelSelectedKeywordsExport.add(jScrollPaneSelectedKeywordsExport, gridBagConstraints);

        jLabelKeywords.setText("Selected keywords:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        jPanelSelectedKeywordsExport.add(jLabelKeywords, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelSummary.add(jPanelSelectedKeywordsExport, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabelInclDaysSummary.setText("Include Day of Week names:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPanel1.add(jLabelInclDaysSummary, gridBagConstraints);

        jLabelUnitsSummary.setText("Units:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPanel1.add(jLabelUnitsSummary, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(jSeparator6, gridBagConstraints);

        jTextAreaSelectedCalendars.setEditable(false);
        jTextAreaSelectedCalendars.setColumns(20);
        jTextAreaSelectedCalendars.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextAreaSelectedCalendars.setLineWrap(true);
        jTextAreaSelectedCalendars.setRows(2);
        jScrollPaneSelectedCalendars.setViewportView(jTextAreaSelectedCalendars);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPaneSelectedCalendars, gridBagConstraints);

        jLabelInclFromToValue.setText("no");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 0);
        jPanel1.add(jLabelInclFromToValue, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(jSeparator5, gridBagConstraints);

        jLabelDateRangeValue.setText("Whole calendar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabelDateRangeValue, gridBagConstraints);

        jLabelInclEmptyRowsSummary.setText("Include empty rows:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPanel1.add(jLabelInclEmptyRowsSummary, gridBagConstraints);

        jLabelInclOutputFSummary.setText("Output format:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabelInclOutputFSummary, gridBagConstraints);

        jLabelDateRangeSummary.setText("Date range:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabelDateRangeSummary, gridBagConstraints);

        jLabelInclFromToSummary.setText("Include from-to:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPanel1.add(jLabelInclFromToSummary, gridBagConstraints);

        jLabelGranularityValue.setText("<val>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabelGranularityValue, gridBagConstraints);

        jLabelRemoveDiacriticsValue.setText("no");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabelRemoveDiacriticsValue, gridBagConstraints);

        jLabelInclDatesSummary.setText("Include dates:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPanel1.add(jLabelInclDatesSummary, gridBagConstraints);

        jLabelInclEventsValue.setText("no");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabelInclEventsValue, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel1.add(jSeparator7, gridBagConstraints);

        jLabelCaseSensitive.setText("Case sensitive:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabelCaseSensitive, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(jSeparator4, gridBagConstraints);

        jLabelInclEmptyRValue.setText("no");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 0);
        jPanel1.add(jLabelInclEmptyRValue, gridBagConstraints);

        jLabelInclEventsSummary.setText("Include events:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabelInclEventsSummary, gridBagConstraints);

        jLabelInclDatesValue.setText("no");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 0);
        jPanel1.add(jLabelInclDatesValue, gridBagConstraints);

        jLabelSelectedCalendar.setText("Selected calendars:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel1.add(jLabelSelectedCalendar, gridBagConstraints);

        jLabelOutputFValue.setText("XLSX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabelOutputFValue, gridBagConstraints);

        jLabelGranularitySummary.setText("Group by:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabelGranularitySummary, gridBagConstraints);

        jLabelInclDaysValue.setText("no");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 0);
        jPanel1.add(jLabelInclDaysValue, gridBagConstraints);

        jLabelCaseSensitiveValue.setText("no");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jLabelCaseSensitiveValue, gridBagConstraints);

        jLabelUnitsValue.setText("<val>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 0);
        jPanel1.add(jLabelUnitsValue, gridBagConstraints);

        jLabelRemoveDiacritics.setText("Remove diacritics:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabelRemoveDiacritics, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        jPanelSummary.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanelExport.add(jPanelSummary, gridBagConstraints);

        jPanelExportOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPanelExportOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Export settings"));
        jPanelExportOptions.setLayout(new java.awt.GridBagLayout());

        jCheckBoxSave.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxSave.setText("save");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        jPanelExportOptions.add(jCheckBoxSave, gridBagConstraints);

        jLabelOption.setText("Option:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelExportOptions.add(jLabelOption, gridBagConstraints);

        jCheckBoxOpen.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxOpen.setSelected(true);
        jCheckBoxOpen.setText("open");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelExportOptions.add(jCheckBoxOpen, gridBagConstraints);

        jButtonExport.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/krasny/icalstats/gui/icons/spritesexport.png"))); // NOI18N
        jButtonExport.setText("EXPORT");
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelExportOptions.add(jButtonExport, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelExportOptions.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelExport.add(jPanelExportOptions, gridBagConstraints);

        jTabbedPaneMain.addTab("Export", jPanelExport);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jTabbedPaneMain, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generationFinished(File output_file){
        String err_text = "";
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setMultiSelectionEnabled(false);
        jfc.setAcceptAllFileFilterUsed(false);
        if(jRadioButtonXls.isSelected())
            jfc.setFileFilter(new FileNameExtensionFilter("Excel 97-03 (*.xls)", "xls"));
        else if(jRadioButtonXlsx.isSelected())
            jfc.setFileFilter(new FileNameExtensionFilter("Excel 2007 (*.xlsx)", "xlsx"));
        else
            jfc.setFileFilter(new FileNameExtensionFilter("HTML file (*.html)", "html"));
        try{
            if(output_file == null) 
                return;
            jfc.setSelectedFile(new File(output_file.getName()));
            if(this.jCheckBoxSave.isSelected()) {
                if(jfc.showDialog(this, "Save") == JFileChooser.APPROVE_OPTION){
                    output_file = FileManager.getInstance().saveFileToFolder(jfc.getSelectedFile().getAbsolutePath(), output_file);
                }
            }
            if(this.jCheckBoxOpen.isSelected()){
                if(Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(output_file);
                else
                    JOptionPane.showMessageDialog(this, "Desktop class for opening files is not supported!", "Error", JOptionPane.ERROR_MESSAGE);
            } 
        } 
        catch(Exception ex){
            cz.krasny.icalstats.data.classes.Logger.appendException(ex);
            err_text = ex.getMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
            err_text += "See log.txt for more details.";
            JOptionPane.showMessageDialog(this, err_text, "Error", JOptionPane.ERROR_MESSAGE);            
        }
    }
    
    private void jButton_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_exitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton_exitActionPerformed
    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed
        if(!this.jCheckBoxOpen.isSelected() && !this.jCheckBoxSave.isSelected()){
            JOptionPane.showMessageDialog(this, "Check open or save option.","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        String err_text = "";
        ExportBackgroundWorker ebw = new ExportBackgroundWorker(this);
        ebw.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals("generation_finished")){
                    generationFinished((File) evt.getNewValue());
                }
            }
        });
        ExportConfiguration export_configuration = new ExportConfiguration();
        export_configuration.setOutputFormat(jRadioButtonHtml.isSelected() ? OutputFormat.HTML : (jRadioButtonXls.isSelected() ? OutputFormat.XLS : OutputFormat.XLSX));
        export_configuration.setGroupBy((GroupBy) jComboBoxGranularity.getSelectedItem());
        export_configuration.setUnit((Units) jComboBoxUnits.getSelectedItem());
        export_configuration.setIncludeEvents(jCheckBoxIncludeEvents.isSelected());
        export_configuration.setIncludeEventDayOfWeek(jCheckBoxIncludeDays.isSelected());
        export_configuration.setIncludeEventFromTo(jCheckBoxIncludeFromTo.isSelected());
        export_configuration.setIncludeEventDate(jCheckBoxIncludeDate.isSelected());
        export_configuration.setIncludeEmptyRows(jCheckBoxIncludeEmptyRows.isSelected());
        export_configuration.setCaseSensitive(jCheckBoxCaseSensitive.isSelected()); 
        export_configuration.setRemoveDiacritics(jCheckBoxRemoveDiacritics.isSelected());
        
        try {
            if(jRadioButtonWholeCalendarExport.isSelected())
                export_configuration.setDateRange(new DateRange(true));
            else{
                Calendar from = jDateChooserDateFromExport.getCalendar();
                Calendar to = jDateChooserDateToExport.getCalendar();
                if(to != null){
                    to.set(Calendar.HOUR_OF_DAY, 23);
                    to.set(Calendar.MINUTE, 59);
                    to.set(Calendar.SECOND, 59);
                }
                export_configuration.setDateRange(new DateRange(from, to));
            }
            ebw.setICalRepresentationsList(model_icr.getSelectedICalRepresentations());
            ebw.setKeywords(model_kw.getKeywords());
            ebw.setExportConfiguration(export_configuration);
            ebw.execute();
        }
        catch (Exception ex) {
            cz.krasny.icalstats.data.classes.Logger.appendException(ex);
            err_text = ex.getMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
            err_text += "See log.txt for more details.";
            JOptionPane.showMessageDialog(this, err_text, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonExportActionPerformed
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        ViewICalFileDetails viewIcalFile = new ViewICalFileDetails(this, true);
        ICalRepresentation icr = viewIcalFile.getICR();
        if(icr == null) return;
        model_icr.add(icr);
        model_calendars.fireContentsChanged();
        
    }//GEN-LAST:event_jButtonAddActionPerformed
    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        int selectedRow = jTableICalRepresentation.convertRowIndexToModel(jTableICalRepresentation.getSelectedRow());
        if(selectedRow == -1) return;
        ICalRepresentation icr = model_icr.getICRData().get(selectedRow);
        ViewICalFileDetails viewICalFileDetails = new ViewICalFileDetails(icr, this, true);
        if(icr == null) return;
        model_icr.setValueAt(icr.getName(), selectedRow, 0);
        model_icr.setValueAt(icr.getUrl() == null ? icr.getPath() : icr.getUrl(), selectedRow, 1);
        model_icr.fireTableDataChanged();
    }//GEN-LAST:event_jButtonEditActionPerformed
    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        int[] ids = jTableICalRepresentation.getSelectedRows();
        int[] convertedRows = new int[ids.length];
        for(int i = 0 ; i < ids.length ; i++){
            convertedRows[i] = jTableICalRepresentation.getRowSorter().convertRowIndexToModel(ids[i]);
        }
        
        Object o = jComboBoxCalendars.getSelectedItem();
        if(o != null){
            ICalRepresentation icr = (ICalRepresentation) o;
            if(model_icr.isCalendarSelected(convertedRows, icr)) jComboBoxCalendars.setSelectedIndex(-1);
        }
        
        model_icr.removeICRList(convertedRows);
    }//GEN-LAST:event_jButtonDeleteActionPerformed
    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        JFileChooser jfc = new JFileChooser(); 
        int dialog_result;
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");
        jfc.setFileFilter(filter); 
        if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            File f = jfc.getSelectedFile();
            if(!f.exists()) return;
            List<Keyword> list = KeywordsManager.loadKeywords(f.getAbsolutePath());
            if(model_kw.getRowCount() > 0 && list.size() > 0){
                dialog_result = JOptionPane.showConfirmDialog(this, "Merge with existing keywords?", "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(dialog_result != JOptionPane.YES_OPTION){
                    model_kw.removeAll();
                }        
            }
            model_kw.addKwList(list);
        }
    }//GEN-LAST:event_jButtonBrowseActionPerformed
    private void jButtonSaveKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveKeywordsActionPerformed
        String path = "";
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv", "csv"));
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setSelectedFile(new File("keywords.csv"));
        jfc.setMultiSelectionEnabled(false);
        if(jfc.showDialog(this, "Save") == JFileChooser.APPROVE_OPTION){
            path = jfc.getSelectedFile().getAbsolutePath();
            if(!path.endsWith(".csv")) 
                path += ".csv";
            if(jfc.getSelectedFile().exists()){
                if(JOptionPane.showConfirmDialog(this, "Overwrite existing file?", "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION){
                    KeywordsManager.saveKeywords(model_kw.getKeywords(), path);
                }
            }
            else{
                KeywordsManager.saveKeywords(model_kw.getKeywords(), path);
            }
        }
    }//GEN-LAST:event_jButtonSaveKeywordsActionPerformed
    private void jButtonDeleteKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteKeywordsActionPerformed
        int[] ids = jTableKeywords.getSelectedRows();
        int[] convertedRows = new int[ids.length];
        for(int i = 0 ; i < ids.length ; i++){
            convertedRows[i] = jTableKeywords.getRowSorter().convertRowIndexToModel(ids[i]);
        }
        model_kw.removeKwList(convertedRows);
    }//GEN-LAST:event_jButtonDeleteKeywordsActionPerformed
    private void jButtonParseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonParseActionPerformed
        Object o = jComboBoxCalendars.getSelectedItem();
        String err_text = "";
        if(o == null) {
            JOptionPane.showMessageDialog(this, "Select source calendar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Calendar c_start = Calendar.getInstance();
        Calendar c_end = Calendar.getInstance();
        ICalRepresentation icr = (ICalRepresentation) o;
        model_kw.removeAll();
        try{
            ICalFile.KeywordsFilter kf = new ICalFile.KeywordsFilter();
            kf.setOccurrence((int) jSpinnerKeywords.getValue());
            kf.setRemoveDiacritics(jCheckBoxRemoveDiacritics.isSelected());
            kf.setCaseSensitive(jCheckBoxCaseSensitive.isSelected());
            if(!jTextFieldRegex.getText().equals("")) 
                kf.setRegex(jTextFieldRegex.getText());
            if(jRadioButtonWholeCalendar.isSelected())
                kf.setWholeCalendar(true);
            else{
                if(jDateChooserDateFrom.getDate() == null) 
                    throw new NullPointerException("Date from can not be null.");
                if(jDateChooserDateTo.getDate() == null) 
                    throw new NullPointerException("Date to can not be null.");
                c_start.setTime(jDateChooserDateFrom.getDate());
                c_end.setTime(jDateChooserDateTo.getDate());
                if(c_start.after(c_end)){
                    JOptionPane.showMessageDialog(this, "Date from can not be after date to.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                c_end.add(Calendar.HOUR, 23);
                c_end.add(Calendar.MINUTE, 59);
                c_end.add(Calendar.SECOND, 59);
                kf.setStartDate(c_start);
                kf.setEndDate(c_end);
            }
            KeywordBackgroundWorker kbw = new KeywordBackgroundWorker(this, icr, model_kw, kf);
            kbw.execute();
        }
        catch(Exception e){
            cz.krasny.icalstats.data.classes.Logger.appendException(e);
            err_text += e.getMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
            err_text += "See log.txt for more details.";
            JOptionPane.showMessageDialog(this, err_text, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonParseActionPerformed
    private void jButtonAddOtherKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddOtherKeywordsActionPerformed
        String other_kw = jTextAreaOtherKeywords.getText();
        String keyword;
        String[] arr = other_kw.split(";");
        List<Keyword> list_kw = new ArrayList<>();
        for(String kw: arr){
            keyword = kw.trim();
            if(keyword.equals("")) 
                continue;
            if(!jCheckBoxCaseSensitive.isSelected())
                keyword = keyword.toLowerCase();
            if(jCheckBoxRemoveDiacritics.isSelected())
                keyword = Normalizer.normalize(keyword, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
            list_kw.add(new Keyword(keyword, 0));
        }
        model_kw.addKwList(list_kw);
        jTextAreaOtherKeywords.setText("");
    }//GEN-LAST:event_jButtonAddOtherKeywordsActionPerformed
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        String err_text = "";
        try {
            XMLManager xml_manager = XMLManager.getInstance();
            xml_manager.saveICalRepresentations(model_icr.getICRData());
            JOptionPane.showMessageDialog(this, "Saved.", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            cz.krasny.icalstats.data.classes.Logger.appendException(ex);
            err_text = ex.getMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
            err_text += "See log.txt for more details.";
            JOptionPane.showMessageDialog(this, err_text, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed
    private void jDateChooserDateFromPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooserDateFromPropertyChange
        jRadioButtonDateFrom.setSelected(true);
        jRadioButtons_dateRange(null);
    }//GEN-LAST:event_jDateChooserDateFromPropertyChange
    private void jRadioButtons_outputFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtons_outputFormatActionPerformed
        if(jRadioButtonXls.isSelected())
            jLabelOutputFValue.setText(jRadioButtonXls.getText());
        else if(jRadioButtonXlsx.isSelected())
            jLabelOutputFValue.setText(jRadioButtonXlsx.getText());
        else if(jRadioButtonHtml.isSelected())
            jLabelOutputFValue.setText(jRadioButtonHtml.getText());
    }//GEN-LAST:event_jRadioButtons_outputFormatActionPerformed
    private void jRadioButtons_dateRange(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtons_dateRange
        if(jRadioButtonWholeCalendarExport.isSelected())
            jLabelDateRangeValue.setText("Whole calendar");
        else if(jRadioButtonDateFromExport.isSelected()){
            String text = "";
            text += jDateChooserDateFromExport.getCalendar() == null ? "null" : DateTimeFormat.forPattern("dd.MM.yyyy").print(jDateChooserDateFromExport.getCalendar().getTime().getTime());
            text += " - ";
            text += jDateChooserDateToExport.getCalendar() == null ? "null" : DateTimeFormat.forPattern("dd.MM.yyyy").print(jDateChooserDateToExport.getCalendar().getTime().getTime());
            jLabelDateRangeValue.setText(text);
        }
    }//GEN-LAST:event_jRadioButtons_dateRange
    private void jDateChooserDatesExportPropertyChanged(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooserDatesExportPropertyChanged
        if(evt.getPropertyName().contains("date")){
            jRadioButtonDateFromExport.setSelected(true);
            jRadioButtons_dateRange(null);
        }
    }//GEN-LAST:event_jDateChooserDatesExportPropertyChanged
    private void jComboBoxGranularityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGranularityActionPerformed
        GroupBy g = (GroupBy) jComboBoxGranularity.getSelectedItem();
        if(g != null)
            jLabelGranularityValue.setText(g.name());
        else
            jLabelGranularityValue.setText("<val>");
    }//GEN-LAST:event_jComboBoxGranularityActionPerformed
    private void jComboBoxUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUnitsActionPerformed
        Units u = (Units) jComboBoxUnits.getSelectedItem();
        if(u != null)
            jLabelUnitsValue.setText(u.name());
        else
            jLabelUnitsValue.setText("<val>");
    }//GEN-LAST:event_jComboBoxUnitsActionPerformed
    private void jCheckBoxesIncludingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxesIncludingStateChanged
        if(jCheckBoxIncludeDate.isSelected() && jCheckBoxIncludeDate.isEnabled())
            jLabelInclDatesValue.setText("yes");
        else
            jLabelInclDatesValue.setText("no");
        if(jCheckBoxIncludeDays.isSelected() && jCheckBoxIncludeDays.isEnabled())
            jLabelInclDaysValue.setText("yes");
        else
            jLabelInclDaysValue.setText("no");
        if(jCheckBoxIncludeEmptyRows.isSelected())
            jLabelInclEmptyRValue.setText("yes");
        else
            jLabelInclEmptyRValue.setText("no");
        if(jCheckBoxIncludeEvents.isSelected()){
            jLabelInclEventsValue.setText("yes");
            jCheckBoxIncludeDate.setEnabled(true);
            jCheckBoxIncludeDays.setEnabled(true);
            jCheckBoxIncludeFromTo.setEnabled(true);
        }
        else{
            jLabelInclEventsValue.setText("no");
            jCheckBoxIncludeDate.setEnabled(false);
            jCheckBoxIncludeDays.setEnabled(false);
            jCheckBoxIncludeFromTo.setEnabled(false);            
        }
        if(jCheckBoxIncludeFromTo.isSelected() && jCheckBoxIncludeFromTo.isEnabled())
            jLabelInclFromToValue.setText("yes");
        else
            jLabelInclFromToValue.setText("no");      
        if(jCheckBoxCaseSensitive.isSelected())
            jLabelCaseSensitiveValue.setText("yes");
        else
            jLabelCaseSensitiveValue.setText("no");
        if(jCheckBoxRemoveDiacritics.isSelected())
            jLabelRemoveDiacriticsValue.setText("yes");
        else
            jLabelRemoveDiacriticsValue.setText("no");
    }//GEN-LAST:event_jCheckBoxesIncludingStateChanged
    private void jTableICalRepresentationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableICalRepresentationMouseClicked
        if(evt.getClickCount() == 2){
            /* Handle doubleclick and open ICR detail */
            int row = jTableICalRepresentation.convertRowIndexToModel(jTableICalRepresentation.rowAtPoint(evt.getPoint()));
            ICalRepresentation icr = model_icr.getICRData().get(row);
            ViewICalFileDetails viewICalFileDetails = new ViewICalFileDetails(icr, this, true);
            if(icr == null) return;
            model_icr.setValueAt(icr.getName(), row, 1);
            model_icr.setValueAt(icr.getUrl() == null ? icr.getPath() : icr.getUrl(), row, 2);
            model_icr.fireTableDataChanged();
            model_calendars.fireContentsChanged();
        }
    }//GEN-LAST:event_jTableICalRepresentationMouseClicked

    private void jTableICalRepresentation_valueChanged(){
        String selected_calendars = "";
        boolean tmp = false;
        for(ICalRepresentation icr : model_icr.getICRData()){
            if(icr.isSelected()){
                tmp = true;
                selected_calendars += icr.getName()+", ";
            }
        }
        if(tmp)
            selected_calendars = selected_calendars.substring(0, selected_calendars.length() - 2);//remove last two chars
        jTextAreaSelectedCalendars.setText(selected_calendars);        
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupGranularity;
    private javax.swing.ButtonGroup buttonGroupUnits;
    private javax.swing.ButtonGroup buttonGroup_dateKeywords;
    private javax.swing.ButtonGroup buttonGroup_datesExport;
    private javax.swing.ButtonGroup buttonGroup_format;
    private javax.swing.ButtonGroup buttonGroup_source;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAddOtherKeywords;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDeleteKeywords;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonParse;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSaveKeywords;
    private javax.swing.JButton jButton_exit;
    private javax.swing.JCheckBox jCheckBoxCaseSensitive;
    private javax.swing.JCheckBox jCheckBoxIncludeDate;
    private javax.swing.JCheckBox jCheckBoxIncludeDays;
    private javax.swing.JCheckBox jCheckBoxIncludeEmptyRows;
    private javax.swing.JCheckBox jCheckBoxIncludeEvents;
    private javax.swing.JCheckBox jCheckBoxIncludeFromTo;
    private javax.swing.JCheckBox jCheckBoxOpen;
    private javax.swing.JCheckBox jCheckBoxRemoveDiacritics;
    private javax.swing.JCheckBox jCheckBoxSave;
    private javax.swing.JComboBox jComboBoxCalendars;
    private javax.swing.JComboBox jComboBoxGranularity;
    private javax.swing.JComboBox jComboBoxUnits;
    private com.toedter.calendar.JDateChooser jDateChooserDateFrom;
    private com.toedter.calendar.JDateChooser jDateChooserDateFromExport;
    private com.toedter.calendar.JDateChooser jDateChooserDateTo;
    private com.toedter.calendar.JDateChooser jDateChooserDateToExport;
    private javax.swing.JLabel jLabelCaseSensitive;
    private javax.swing.JLabel jLabelCaseSensitiveValue;
    private javax.swing.JLabel jLabelDateRangeSummary;
    private javax.swing.JLabel jLabelDateRangeValue;
    private javax.swing.JLabel jLabelDateTo;
    private javax.swing.JLabel jLabelFormatExport;
    private javax.swing.JLabel jLabelGranularity;
    private javax.swing.JLabel jLabelGranularitySummary;
    private javax.swing.JLabel jLabelGranularityValue;
    private javax.swing.JLabel jLabelInclDatesSummary;
    private javax.swing.JLabel jLabelInclDatesValue;
    private javax.swing.JLabel jLabelInclDaysSummary;
    private javax.swing.JLabel jLabelInclDaysValue;
    private javax.swing.JLabel jLabelInclEmptyRValue;
    private javax.swing.JLabel jLabelInclEmptyRowsSummary;
    private javax.swing.JLabel jLabelInclEventsSummary;
    private javax.swing.JLabel jLabelInclEventsValue;
    private javax.swing.JLabel jLabelInclFromToSummary;
    private javax.swing.JLabel jLabelInclFromToValue;
    private javax.swing.JLabel jLabelInclOutputFSummary;
    private javax.swing.JLabel jLabelKeywords;
    private javax.swing.JLabel jLabelKeywordsCount;
    private javax.swing.JLabel jLabelKeywordsCountValue;
    private javax.swing.JLabel jLabelMinimalOccurrence;
    private javax.swing.JLabel jLabelOption;
    private javax.swing.JLabel jLabelOtherKeywords;
    private javax.swing.JLabel jLabelOutputFValue;
    private javax.swing.JLabel jLabelRegex;
    private javax.swing.JLabel jLabelRemoveDiacritics;
    private javax.swing.JLabel jLabelRemoveDiacriticsValue;
    private javax.swing.JLabel jLabelSelectedCalendar;
    private javax.swing.JLabel jLabelUnits;
    private javax.swing.JLabel jLabelUnitsSummary;
    private javax.swing.JLabel jLabelUnitsValue;
    private javax.swing.JLabel jLabel_dateToExport;
    private javax.swing.JLabel jLabel_name;
    private javax.swing.JLabel jLabel_name_real;
    private javax.swing.JLabel jLabel_selectedText;
    private javax.swing.JLabel jLabel_source;
    private javax.swing.JLabel jLabel_source_real;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelButtonsKeywords;
    private javax.swing.JPanel jPanelCalendarRepresentations;
    private javax.swing.JPanel jPanelExport;
    private javax.swing.JPanel jPanelExportOptions;
    private javax.swing.JPanel jPanelKeywords;
    private javax.swing.JPanel jPanelOptions;
    private javax.swing.JPanel jPanelOtherKeywords;
    private javax.swing.JPanel jPanelSelectedKeywordsExport;
    private javax.swing.JPanel jPanelStatistics;
    private javax.swing.JPanel jPanelSummary;
    private javax.swing.JPanel jPanel_MAIN;
    private javax.swing.JPanel jPanel_bottom_end;
    private javax.swing.JPanel jPanel_selected_file;
    private javax.swing.JRadioButton jRadioButtonDateFrom;
    private javax.swing.JRadioButton jRadioButtonDateFromExport;
    private javax.swing.JRadioButton jRadioButtonFromCalendar;
    private javax.swing.JRadioButton jRadioButtonFromFile;
    private javax.swing.JRadioButton jRadioButtonHtml;
    private javax.swing.JRadioButton jRadioButtonWholeCalendar;
    private javax.swing.JRadioButton jRadioButtonWholeCalendarExport;
    private javax.swing.JRadioButton jRadioButtonXls;
    private javax.swing.JRadioButton jRadioButtonXlsx;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneICalRepresentation;
    private javax.swing.JScrollPane jScrollPaneKeywords;
    private javax.swing.JScrollPane jScrollPaneOtherKeywords;
    private javax.swing.JScrollPane jScrollPaneSelectedCalendars;
    private javax.swing.JScrollPane jScrollPaneSelectedKeywordsExport;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSpinner jSpinnerKeywords;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    private javax.swing.JTable jTableICalRepresentation;
    private javax.swing.JTable jTableKeywords;
    private javax.swing.JTable jTableKeywordsExport;
    private javax.swing.JTextArea jTextAreaOtherKeywords;
    private javax.swing.JTextArea jTextAreaSelectedCalendars;
    private javax.swing.JTextField jTextFieldRegex;
    // End of variables declaration//GEN-END:variables
    
}