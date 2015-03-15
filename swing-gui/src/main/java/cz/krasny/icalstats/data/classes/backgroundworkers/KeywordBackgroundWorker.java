package cz.krasny.icalstats.data.classes.backgroundworkers;

import cz.krasny.icalstats.core.managers.ICSFileManager;
import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.ICalRepresentation;
import cz.krasny.icalstats.data.classes.models.KeywordsTableModel;
import cz.krasny.icalstats.gui.view.ViewWaitingDialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Class used to parse keywords in other threads.
 * @author Tomas Krasny
 */
public class KeywordBackgroundWorker extends SwingWorker<ICalFile, Void>{

    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    private ICalRepresentation icr = null;
    private ICalFile.KeywordsFilter kf = null;
    private KeywordsTableModel model_kw = null;
    private ViewWaitingDialog dialog = null;
    private Exception ex = null;
    
    public KeywordBackgroundWorker(JFrame parent, ICalRepresentation _icr, KeywordsTableModel _model_kw, ICalFile.KeywordsFilter _kf){
        icr = _icr;
        kf = _kf;
        model_kw = _model_kw;
        initWaitDialog(parent);
    }
    
    /* Inits and shows dialog with progress. */
    private void initWaitDialog(JFrame parent){
        dialog = new ViewWaitingDialog(parent, "Parsing keywords...");
        dialog.setLocationRelativeTo(null);
        dialog.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals("cancel_generating")){
                    cancel(true);
                }
            }
        });         
        dialog.setVisible(true);
        dialog.pack();        
    }
    
    /* Does the main work in next thread. */    
    @Override
    public ICalFile doInBackground(){
        ICalFile icf = null;
        try {
            icf = ICSFileManager.getInstance().loadICalFile(icr);
            model_kw.addKwList(icf.getKeywordsList(kf));
        } catch (Exception _ex) {
            ex = _ex;
        }
        return icf;
    }
    
    /* Called after the task is finished. */    
    @Override
    public void done(){
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
