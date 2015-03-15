package cz.krasny.icalstats.data.classes.output;

import cz.krasny.icalstats.data.classes.DateRange;

/***
 * Class representing export configuration.
 * @author Tomas Krasny
 */
public class ExportConfiguration {
    
    private boolean include_days_of_week = false;
    private boolean include_event_dates = false;
    private boolean include_empty_rows = false;
    private OutputFormat output_format = null;
    private boolean remove_diacritics = true;
    private boolean include_from_to = false;
    private boolean case_sensitive = false;
    private boolean include_events = false;
    private DateRange date_range = null;
    private GroupBy group_by = null;
    private Units unit = null;

    public ExportConfiguration() { }

    public boolean isIncludeEventDate(){
        return include_event_dates;
    }

    public boolean isRemoveDiacritics() {
        return remove_diacritics;
    }

    public boolean isCaseSensitive() {
        return case_sensitive;
    }

    public boolean isIncludeEmptyRows() {
        return include_empty_rows;
    }
    
    public boolean isIncludeEventDayOfWeek() {
        return include_days_of_week;
    }

    public boolean isIncludeEvents() {
        return include_events;
    }

    public boolean isIncludeEventFromTo() {
        return include_from_to;
    }    
    
    public void setCaseSensitive(boolean case_sensitive) {
        this.case_sensitive = case_sensitive;
    }

    public void setRemoveDiacritics(boolean remove_diacritics) {
        this.remove_diacritics = remove_diacritics;
    }
    
    public void setIncludeEventDate(boolean include_event_dates) {
        this.include_event_dates = include_event_dates;
    }
    
    public void setIncludeEmptyRows(boolean include_empty_rows) {
        this.include_empty_rows = include_empty_rows;
    }
    
    public void setIncludeEventDayOfWeek(boolean include_days) {
        this.include_days_of_week = include_days;
    }
    
    public void setIncludeEvents(boolean include_events) {
        this.include_events = include_events;
    }

    public void setIncludeEventFromTo(boolean include_from_to) {
        this.include_from_to = include_from_to;
    }
    
    public void setGroupBy(GroupBy groupBy) {
        this.group_by = groupBy;
    }
    
    public void setDateRange(DateRange date_range) {
        this.date_range = date_range;
    }    
    
    public void setOutputFormat(OutputFormat output_format) {
        this.output_format = output_format;
    }    
    
    public void setUnit(Units unit) {
        this.unit = unit;
    }
    
    public Units getUnit() {
        return unit;
    }

    public GroupBy getGroupBy() {
        return group_by;
    }
    
    public OutputFormat getOutputFormat() {
        return output_format;
    }
    
    public DateRange getDateRange() {
        return date_range;
    }
}
