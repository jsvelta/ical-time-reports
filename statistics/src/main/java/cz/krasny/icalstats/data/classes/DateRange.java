/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.krasny.icalstats.data.classes;

import java.util.Calendar;

/**
 * Class representing date range.
 * @author Tomas
 */
public class DateRange {
    
    private Calendar date_from;
    private Calendar date_to;
    private boolean whole_calendar;
    
    public DateRange(Calendar from, Calendar to){
        date_from = from;
        date_to = to;
    }
    
    public DateRange(boolean whole_calendar){
        this.whole_calendar = whole_calendar;
    }

    public Calendar getDateTo() {
        return date_to;
    }

    public Calendar getDateFrom() {
        return date_from;
    }

    public boolean isIncludedWholeCalendar() {
        return whole_calendar;
    }

    public void setDateFrom(Calendar date_from) {
        this.date_from = date_from;
    }

    public void setDateTo(Calendar date_to) {
        this.date_to = date_to;
    }

    public void setWholeCalendar(boolean whole_calendar) {
        this.whole_calendar = whole_calendar;
    }
    
    
}
