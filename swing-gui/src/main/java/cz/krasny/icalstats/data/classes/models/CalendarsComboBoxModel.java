package cz.krasny.icalstats.data.classes.models;

import cz.krasny.icalstats.data.classes.ICalRepresentation;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * Class representing data model for comboboxes with calendars.
 * @author Tomas Krasny
 */
public class CalendarsComboBoxModel extends AbstractListModel implements ComboBoxModel{

    private List<ICalRepresentation> icr_data = null;
    private ICalRepresentation selected_icr = null;
    
    public CalendarsComboBoxModel(List<ICalRepresentation> icr_data){
        this.icr_data = icr_data;
    }
    
    public void fireContentsChanged(){
        fireContentsChanged(this, 0, 0);
    }
    
    @Override
    public void setSelectedItem(Object anItem) {
        selected_icr = (ICalRepresentation) anItem;
        
    }

    @Override
    public Object getSelectedItem() {
        return selected_icr;
    }

    @Override
    public int getSize() {
        return icr_data.size();
    }

    @Override
    public Object getElementAt(int index) {
        return icr_data.get(index);
    }    
}
