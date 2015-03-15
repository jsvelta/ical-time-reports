package cz.krasny.icalstats.data.classes;

import cz.krasny.icalstats.data.classes.output.Units;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

/***
 * Class representing wrapper of VEVENT icalendar component.
 * @author Tomas
 */
public class MyEvent implements Comparable<MyEvent>{
    
    /* VEVENT */
    private final VEvent event;
    private List<String> words;
    
    private static final int MILISECONDS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    
    public MyEvent(VEvent event){
        this.event = event;
    }
    
    public VEvent getEvent(){
        return this.event;
    }
    
    public List<String> getWords(){
        return words;
    }
    
    /* Calculates events duration and returns it in specified unit. */
    public double getDuration(Units unit) throws IllegalArgumentException{
        Calendar from = null, to = null;
        double duration = 0;
        from = Calendar.getInstance();
        to = Calendar.getInstance();
        from.setTime(event.getStartDate().getDate());
        to.setTime(event.getEndDate().getDate());
        duration = to.getTime().getTime()/MILISECONDS_PER_MINUTE;
        duration -= from.getTime().getTime()/MILISECONDS_PER_MINUTE;
        switch(unit){
            case Minutes: ; break;
            case Hours: duration = duration/MINUTES_PER_HOUR; break;
            case Days: duration = duration/MINUTES_PER_HOUR/HOURS_PER_DAY; break;
            default: throw new IllegalArgumentException("Unknow unit: "+unit);
        }
        return duration;
    }
    
    /* Creates array with words from event's summary. Case sensitive and diacritics settings is applied. */
    public void loadWords(boolean case_sensitive, boolean remove_diacritics){
        String summary = getSummary();
        if(!case_sensitive)
            summary = summary.toLowerCase();
        if(remove_diacritics)
            summary = Normalizer.normalize(summary, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        summary = summary.replaceAll("[\\\\()/*+_.,?!:;(){}|<>\\[\"`¨'\\&^!@=#$~%§\\]-]","");
        summary = summary.replace("\"", "");
        words = Arrays.asList(summary.split(" "));
    }
        
    /* Returns start date of this event. */
    public Calendar getStartDate(){
        Calendar c = Calendar.getInstance();
        c.setTime(this.event.getStartDate().getDate());
        return c;
    }
    
    /* Returns start date in specified format. */
    public String getStartDateWithSpecificFormat(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(getStartDate().getTime());
    }

    /* Returns end date of this event. */
    public String getEndDateWithSpecificFormat(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(getEndDate().getTime());
    }  
    
    /* Returns end date in specified format. */
    public Calendar getEndDate(){
        Calendar c = Calendar.getInstance();
        c.setTime(this.event.getEndDate().getDate());
        return c;        
    }
    
    public int getStartYear(){
        return getStartDate().get(Calendar.YEAR);
    }
    
    public int getEndYear(){
        return getEndDate().get(Calendar.YEAR);
    }
    
    public int getStartMonth(){
        return getStartDate().get(Calendar.MONTH) + 1;
    }
    
    public int getEndMonth(){
        return getEndDate().get(Calendar.MONTH) + 1;
    }
    
    public int getStartHours(){
        return getStartDate().get(Calendar.HOUR_OF_DAY);
    }
    
    public int getEndHours(){
        return getEndDate().get(Calendar.HOUR_OF_DAY);
    }
    
    public int getStartMinutes(){
        return getStartDate().get(Calendar.MINUTE);
    }
    
    public int getEndMinutes(){
        return getEndDate().get(Calendar.MINUTE);
    }
    
    public String getSummary(){
        return this.event.getProperty(Property.SUMMARY).getValue();
    }
    
    @Override
    public int compareTo(MyEvent arg0) {
        return this.event.getStartDate().getDate().compareTo(arg0.event.getStartDate().getDate());
    }
    
    @Override
    public String toString(){
        return this.event.toString();
    }
}