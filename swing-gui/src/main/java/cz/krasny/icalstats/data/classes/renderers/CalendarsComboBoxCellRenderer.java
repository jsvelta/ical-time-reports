package cz.krasny.icalstats.data.classes.renderers;

import cz.krasny.icalstats.data.classes.ICalRepresentation;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Class representing a renderer for combobox with calendars.
 * @author Tomas Krasny
 */
public class CalendarsComboBoxCellRenderer implements ListCellRenderer{

    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        ICalRepresentation icr;
        String display_string = "";
        JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if(value != null) {
            icr = (ICalRepresentation) value;
            display_string += icr.getName();
        }
        label.setText(display_string);
        
        return label;
    }
    
}
