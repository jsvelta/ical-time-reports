package cz.krasny.icalstats.data.classes.models;

import cz.krasny.icalstats.data.classes.Keyword;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Class representing data model for tables with keywords.
 * @author Tomas Krasny
 */
public class KeywordsTableModel extends AbstractTableModel{
    
    private ArrayList<Keyword> kw_data = null;
    private ArrayList<String> col_names = null;
    
    public KeywordsTableModel(){
        kw_data = new ArrayList<>();
        col_names = new ArrayList<>();
    }
    
    public KeywordsTableModel(ArrayList<Keyword> kw_data, ArrayList<String> col_names){
        this.kw_data = kw_data;
        this.col_names = col_names;
    }
    
    public void add(Keyword kw){
        kw_data.add(kw);
        fireTableDataChanged();
    }
    
    public void addKwList(List<Keyword> kw_list){
        Iterator<Keyword> i = null;
        Keyword kw_new;
        for(Keyword kw_old: kw_data){
            i = kw_list.iterator();
            while(i.hasNext()){
                kw_new = i.next();
                if(kw_old.getKeyword().equals(kw_new.getKeyword())){
                    kw_old.setOccurrence(kw_old.getOccurrence() + 1);
                    i.remove();
                }
            }
        }
        kw_data.addAll(kw_list);
        fireTableDataChanged();
    }
    
    public void removeKW(Keyword kw){
        kw_data.remove(kw);
    }
    
    public void removeKwList(List<Keyword> kw_list){
        kw_data.removeAll(kw_list);
        fireTableDataChanged();
    }
    
    public void removeKwList(int[] ids){
        List<Keyword> remove_list = new ArrayList<>();
        for(Integer i: ids){
            remove_list.add(kw_data.get(i));
        }
        removeKwList(remove_list);
    }
    
    public void removeAll(){
        kw_data.clear();
        fireTableDataChanged();
    }
    
    public List<Keyword> getKeywords(){
        return kw_data;
    }
    
    @Override
    public String getColumnName(int column){
        return col_names.get(column);
    }
    
    @Override
    public int getRowCount() {
        return kw_data.size();
    }
    
    @Override
    public int getColumnCount() {
        return col_names.size();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Keyword kw = kw_data.get(rowIndex);
        switch(columnIndex){
            case 0: return kw.getKeyword();
            case 1: return kw.getOccurrence();
        }
        return "";
    }
    
    @Override
    public Class getColumnClass(int column){
        switch(column){
            case 0: return String.class;
            case 1: return Integer.class;
            default: return String.class;
        }
    }
}
