package cz.krasny.icalstats.data.classes.backgroundworkers;

import cz.krasny.icalstats.core.managers.ICSFileManager;
import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.ICalRepresentation;
import cz.krasny.icalstats.data.classes.Keyword;
import cz.krasny.icalstats.data.classes.output.ExportConfiguration;
import cz.krasny.icalstats.data.classes.output.Exporter;
import cz.krasny.icalstats.gui.view.ViewWaitingDialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Class used to generate statistics in other threads.
 * @author Tomas Krasny
 */
public class ExportBackgroundWorker extends SwingWorker<File, Void>{
    
    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    private List<ICalRepresentation> ical_reps = null;
    private ViewWaitingDialog dialog = null;
    private List<Keyword> keywords = null;
    private ExportConfiguration ec = null;
    private Exporter exporter = null;
    private Exception ex = null;
    
    public ExportBackgroundWorker(JFrame parent){
        exporter = new Exporter();
        initWaitDialog(parent);
    }
    
    public void setExportConfiguration(ExportConfiguration _ec){
        ec = _ec;
    }
    
    public void setICalRepresentationsList(List<ICalRepresentation> _ical_reps){
        ical_reps = _ical_reps;
    }
    
    public void setKeywords(List<Keyword> _keywords){
        keywords = _keywords;
    }

    /* Inits and shows dialog with progress. */
    private void initWaitDialog(JFrame parent){
        String title = "Generating statistics...";
        dialog = new ViewWaitingDialog(parent, title);
        dialog.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals("cancel_generating")){
                    cancel(true);
                }
            }
        }); 
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.pack();          
    }
    
    /* Does the main work in next thread. */
    @Override
    protected File doInBackground() {
        File file = null;
        List<ICalFile> list = new ArrayList<>();
        ICalFile icf = null;
        try {
            for(ICalRepresentation icr: ical_reps){
                icf = ICSFileManager.getInstance().loadICalFile(icr);
                icf.parseWordsInEvents(ec.isCaseSensitive(), ec.isRemoveDiacritics());
                list.add(icf);
            }
            file = exporter.export(list, keywords, ec);
        } catch (Exception _ex) {
            ex = _ex;
        }
        return file;
    }   
    
    /* Called after the task is finished. */
    @Override
    protected void done(){
        if(ex == null){
            try {
                if(isCancelled())
                    firePropertyChange("generation_finished", null, null);
                else
                    firePropertyChange("generation_finished", null, get());
            } catch (Exception _ex) {
                ex = _ex;
            } 
        }
        if(ex != null){
            String err_text = "";
            cz.krasny.icalstats.data.classes.Logger.appendException(ex);
            err_text += ex.getMessage() + LINE_SEPARATOR + LINE_SEPARATOR;
            err_text += "See log.txt for more details.";
            JOptionPane.showMessageDialog(null, err_text, "Error", JOptionPane.ERROR_MESSAGE);            
        }
        dialog.dispose();
    }
    
}