package cz.krasny.icalstats.data.classes.models;

import cz.krasny.icalstats.data.classes.ICalRepresentation;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Class representing data model for tables with calendars.
 * @author Tomas Krasny
 */
public class ICalRepresentationTableModel extends AbstractTableModel{

    private List<ICalRepresentation> icr_data = null;
    private List<String> col_names = null;
    
    public ICalRepresentationTableModel(){
        icr_data = new ArrayList<>();
        col_names = new ArrayList<>();
    }

    public ICalRepresentationTableModel(List<ICalRepresentation> icr_data, List<String> col_names) {
        this.icr_data = icr_data;
        this.col_names = col_names;
    }
    
    public void add(ICalRepresentation icr){
        icr_data.add(icr);
        fireTableDataChanged();
    }    
    
    public void addAll(List<ICalRepresentation> icr_list){
        icr_data.addAll(icr_list);
        fireTableDataChanged();
    }
    
    public void removeICR(ICalRepresentation icr){
        icr_data.remove(icr);
    }
    
    public void removeICRList(List<ICalRepresentation> icr_list){
        icr_data.removeAll(icr_list);
        fireTableDataChanged();
    }
    
    public void removeAll(){
        icr_data.clear();
        fireTableDataChanged();
    }
    
    public void removeICRList(int[] ids) {
        List<ICalRepresentation> remove_list = new ArrayList<>();
        for(Integer i: ids){
            remove_list.add(icr_data.get(i));
        }
        removeICRList(remove_list);
    }
    
    public List<ICalRepresentation> getICRData(){
        return icr_data;
    }
    
    @Override
    public String getColumnName(int column){
        return col_names.get(column);
    }
    
    @Override
    public int getRowCount() {
        return icr_data.size();
    }

    @Override
    public int getColumnCount() {
        return col_names.size();
    }
    
    @Override
    public boolean isCellEditable(int row, int column){
        return (column == 0);
    }
    
    @Override
    public Class getColumnClass(int column){
        switch(column){
            case 0: return Boolean.class;
            case 1: return String.class;
            case 2: return String.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ICalRepresentation icr = icr_data.get(rowIndex);
        switch(columnIndex){
            case 0: return icr.isSelected();
            case 1: return icr.getName(); 
            case 2: return icr.getUrl() == null ? icr.getPath() : icr.getUrl().toString();
        }
        return "";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ICalRepresentation icr = icr_data.get(rowIndex);
        switch(columnIndex){
            case 0: icr.setSelected(Boolean.parseBoolean(aValue.toString())); break;
        }
        fireTableDataChanged();
    }

    public List<ICalRepresentation> getSelectedICalRepresentations(){
        List<ICalRepresentation> list = new ArrayList<>();
        for(ICalRepresentation icr: icr_data)
            if(icr.isSelected()) 
                list.add(icr);
        return list;
    }
    
    /* Returns true if specified ical representation is selected. */
    public boolean isCalendarSelected(int[] convertedRows, ICalRepresentation icr) {
        for(int i = 0 ; i < convertedRows.length ; i++)
            if(icr_data.get(convertedRows[i]).equals(icr))
                return true;
        return false;
    }
}
