package cz.krasny.icalstats.data.classes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;

/***
 * Class representing ical file.
 * 
 * @author Tomas Krasny
 */
public final class ICalFile {
    
    /* Calendar instance. */
    net.fortuna.ical4j.model.Calendar calendar = null;
    
    /* Calendar's events. */
    private TreeSet<MyEvent> events = null;
    
    /* Creates calendar instance. */
    public ICalFile(net.fortuna.ical4j.model.Calendar calendar) throws Exception{
        this.calendar = calendar;
        loadEvents();
    }
    
    public TreeSet<MyEvent> getEvents(){
        return this.events;
    }
    
    /* Creates array with words from event's summary. Case sensitive and diacritics settings is applied. */
    public void parseWordsInEvents(boolean case_sensitive, boolean ignore_diacritics){
        for(MyEvent event: events)
            event.loadWords(case_sensitive, ignore_diacritics);
    }
    
    /* Returns list with keywords according to specified filter. */
    public List<Keyword> getKeywordsList(KeywordsFilter filter){
        HashMap<String, Keyword> map = new HashMap<>();
        Set<Keyword> keywords_set = new HashSet<>();
        Pattern pattern = null;
        Keyword keyword;
        List<String> words = null;
        parseWordsInEvents(filter.case_sensitive, filter.ignore_diacritics);
        for(MyEvent event: events){
            words = event.getWords();
            if(filter.isWholeCalendar() || (event.getStartDate().after(filter.getStartDate()) && event.getStartDate().before(filter.getEndDate()))){
                //date is OK
                for (String word : words) {
                    if(word.equals("")) 
                        continue;
                    keyword = map.get(word);
                    if (keyword != null)
                        keyword.setOccurrence(keyword.getOccurrence() + 1);
                    else
                        keyword = new Keyword(word, 1);
                    map.put(word, keyword);
                    if(keyword.getOccurrence() >= filter.getOccurrence()) 
                        keywords_set.add(keyword);
                }
            }
        }
        //check if keywords matches to regex
        if((pattern = filter.getPattern()) != null){
            Iterator it = keywords_set.iterator();
            while(it.hasNext())
                if(!pattern.matcher(((Keyword) it.next()).getKeyword()).matches()) 
                    it.remove();
        }
        return new ArrayList<>(keywords_set);
    }
    
    /* Loads events from source calendar. */
    private void loadEvents() throws IOException, ParserException, ParseException, URISyntaxException{
        ComponentList cl = this.calendar.getComponents(Component.VEVENT);
        VEvent event;
        events = new TreeSet<>();
        for (Object cl1 : cl) {
            event = (VEvent) cl1;
            events.add(new MyEvent(event));
        }
        filterInfinityVEvents();
        unwrapRepeatedEvents();
    }
    
    /* Removes infinity events. */
    private void filterInfinityVEvents() throws ParseException{
        ArrayList<MyEvent> infinityEvents = null;
        Recur recurrence = null;
        Property propRRULE = null;
        infinityEvents = new ArrayList<>();
        for(MyEvent event: events){
            propRRULE = event.getEvent().getProperty(Property.RRULE);
            if(propRRULE != null){
                recurrence = new Recur(propRRULE.getValue());
                if(recurrence.getCount() != -1) continue; //jakmile je udalost nekonecna, tato podminka by neplatila, aneb tak poznam udalosti do nekonecna
                if(recurrence.getUntil() != null) continue; //jakmile je udalost nekonecna, tato podminka by neplatila, aneb tak poznam udalosti do nekonecna
                infinityEvents.add(event);
            }
        }
        events.removeAll(infinityEvents);
    }
    
    /* Unwraps repeated events. */
    private void unwrapRepeatedEvents() throws ParseException, IOException, URISyntaxException{
        TreeSet<MyEvent> unwrappedEvents = null;
        Property propRRULE = null;
        Recur recurrence = null;
        unwrappedEvents = new TreeSet<>();
        for(MyEvent event: this.events){
            propRRULE = event.getEvent().getProperty(Property.RRULE);
            if(propRRULE == null) 
                continue;
            recurrence = new Recur(propRRULE.getValue());
            if(recurrence.getCount() > 0) 
                unwrappedEvents.addAll(unwrapRepeatedEventsCOUNT(event, recurrence.getCount()));
            else if(recurrence.getUntil() != null) 
                unwrappedEvents.addAll(unwrapRepeatedEventsUNTIL(event));
        }
        this.events.addAll(unwrappedEvents);
    }
    
    /* Unwraps events with specified count of recurrence. */
    private TreeSet<MyEvent> unwrapRepeatedEventsCOUNT(MyEvent repEvent, int count) throws ParseException, IOException, URISyntaxException{
        Property propRRULE = null;
        Recur recurrence = null;
        VEvent copy = null;
        VEvent repeatedEvent = repEvent.getEvent();
        TreeSet<MyEvent> events = new TreeSet<>();
        net.fortuna.ical4j.model.Date seedSTART = repeatedEvent.getStartDate().getDate();
        net.fortuna.ical4j.model.Date seedEND = repeatedEvent.getEndDate().getDate();
        net.fortuna.ical4j.model.Date start = repeatedEvent.getStartDate().getDate();
        net.fortuna.ical4j.model.Date end = repeatedEvent.getEndDate().getDate();
        propRRULE = repeatedEvent.getProperty(Property.RRULE);
        recurrence = new Recur(propRRULE.getValue());
        for(int i = 0 ; i < count-1 ; i++){
            start = recurrence.getNextDate(seedSTART, start);
            end = recurrence.getNextDate(seedEND, end);
            copy = (VEvent) repeatedEvent.copy();
            copy.getStartDate().setDate(start);
            copy.getEndDate().setDate(end);
            events.add(new MyEvent(copy));
        }
        return events;
    }
    
    /* Unwraps events with specified end date. */
    private TreeSet<MyEvent> unwrapRepeatedEventsUNTIL(MyEvent repEvent) throws ParseException, IOException, URISyntaxException{
        Property propRRULE = null;
        Recur recurrence = null;
        VEvent copy = null;
        net.fortuna.ical4j.model.Date seedSTART = null;
        net.fortuna.ical4j.model.Date seedEND = null;
        net.fortuna.ical4j.model.Date startDate = null;
        net.fortuna.ical4j.model.Date endDate = null;
        VEvent repeatedEvent = repEvent.getEvent();
        TreeSet<MyEvent> events = new TreeSet<>();
        seedSTART = repeatedEvent.getStartDate().getDate();
        startDate = repeatedEvent.getStartDate().getDate();
        seedEND = repeatedEvent.getEndDate().getDate();
        endDate = repeatedEvent.getEndDate().getDate();
        propRRULE = repeatedEvent.getProperty(Property.RRULE);
        recurrence = new Recur(propRRULE.getValue());
        while(true){
            startDate = recurrence.getNextDate(seedSTART, startDate);
            endDate = recurrence.getNextDate(seedEND, endDate);
            if(startDate == null) break;
            if(endDate == null){
                long time = startDate.getTime();
                long duration = seedEND.getTime() - seedSTART.getTime();
                endDate = new DateTime(time + duration);
                copy = (VEvent) repeatedEvent.copy();
                copy.getStartDate().setDate(startDate);
                copy.getEndDate().setDate(endDate);
                events.add(new MyEvent(copy));
                break;
            }
            copy = (VEvent) repeatedEvent.copy();
            copy.getStartDate().setDate(startDate);
            copy.getEndDate().setDate(endDate);
            events.add(new MyEvent(copy));
        }
        return events;
    }
    
    /* Class representing keywords parsing settings. */
    public static class KeywordsFilter{
        
        private boolean ignore_diacritics = false;
        private boolean whole_calendar = false;
        private boolean case_sensitive = false;
        private Calendar start_date = null;
        private Calendar end_date = null;
        private Pattern pattern = null;
        private int occurrence = 1;
        
        public void setRegex(String regex) {
            pattern = Pattern.compile(regex);
        }
        
        public Pattern getPattern() {
            return pattern;
        }
        
        public int getOccurrence() {
            return occurrence;
        }
        
        public void setOccurrence(int min_occurrence) {
            this.occurrence = min_occurrence;
        }
        
        public boolean isWholeCalendar() {
            return whole_calendar;
        }
        
        public void setWholeCalendar(boolean wholeCalendar) {
            this.whole_calendar = wholeCalendar;
        }
        
        public Calendar getEndDate() {
            return end_date;
        }
        
        public Calendar getStartDate() {
            return start_date;
        }
        
        public void setStartDate(Calendar startDate) {
            this.start_date = startDate;
        }
        
        public void setEndDate(Calendar endDate) {
            this.end_date = endDate;
        }

        public boolean isIgnoreDiacritics() {
            return ignore_diacritics;
        }

        public void setRemoveDiacritics(boolean ignore_diacritics) {
            this.ignore_diacritics = ignore_diacritics;
        }

        public boolean isCaseSensitive() {
            return case_sensitive;
        }

        public void setCaseSensitive(boolean case_sensitive) {
            this.case_sensitive = case_sensitive;
        }
    }
}