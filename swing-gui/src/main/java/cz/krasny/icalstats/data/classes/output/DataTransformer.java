package cz.krasny.icalstats.data.classes.output;

import cz.krasny.icalstats.data.classes.DateRange;
import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.MyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Class for preparing data for generating statistic.
 * @author Tomas Krasny
 */
public class DataTransformer {
    
    private static final DateTimeFormatter DATE_FORMAT_DAYS = DateTimeFormat.forPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_FORMAT_WEEKS = DateTimeFormat.forPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_FORMAT_MONTHS = DateTimeFormat.forPattern("MMMM yyyy");    
    
    private TreeSet<MyEvent> events_all = null;
    
    public DataTransformer(List<ICalFile> ical_files){
        this.events_all = new TreeSet<>();
        for(ICalFile icf: ical_files)
            events_all.addAll(icf.getEvents());
    }
    
    /* Returns events within specified date range. */
    public List<MyEvent> getDataPerIndividualEvents(DateRange date_range){
        List<MyEvent> data = new ArrayList<>();
        for(MyEvent event: events_all)
            if(isInDateRange(event, date_range))
                data.add(event);
        return data;
    }

    /* Returns data in form: key example: "3.6.2013", value: list of events within specified date range. */
    public TreeMap<String, List<MyEvent>> getDataPerDays(DateRange date_range){
        String mapkey = "";
        TreeMap<String, List<MyEvent>> data = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return DATE_FORMAT_DAYS.withLocale(Locale.ENGLISH).parseDateTime(o1).compareTo(DATE_FORMAT_DAYS.withLocale(Locale.ENGLISH).parseDateTime(o2));
            }
        });
        List<MyEvent> value = null;
        for(MyEvent event: events_all)
            if(isInDateRange(event, date_range)){
                mapkey = createMapkeyDays(event.getStartDate());
                value = data.get(mapkey);
                if(value == null) value = new ArrayList<>();
                value.add(event);
                data.put(mapkey, value);
            }
        return data;
    }

    /* Returns data in form: key example: "3.6.2013", where 3.6.2013 is Monday. Value: list of events in this week. */
    public TreeMap<String, List<MyEvent>> getDataPerWeeks(DateRange date_range){
        String mapkey = "";
        TreeMap<String, List<MyEvent>> data = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return DATE_FORMAT_WEEKS.withLocale(Locale.ENGLISH).parseDateTime(o1).compareTo(DATE_FORMAT_WEEKS.withLocale(Locale.ENGLISH).parseDateTime(o2));
            }
        });
        List<MyEvent> value = null;
        for(MyEvent event: events_all)
            if(isInDateRange(event, date_range)){
                mapkey = createMapkeyWeeks(event.getStartDate());
                value = data.get(mapkey);
                if(value == null) value = new ArrayList<>();
                value.add(event);
                data.put(mapkey, value);
            }
        return data;
    }

    /* Returns data in form: key example: "June 2013", value: list of events in this month. */
    public TreeMap<String, List<MyEvent>> getDataPerMonths(DateRange date_range){
        String mapkey = "";
        TreeMap<String, List<MyEvent>> data = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    return DATE_FORMAT_MONTHS.withLocale(Locale.ENGLISH).parseDateTime(o1).compareTo(DATE_FORMAT_MONTHS.withLocale(Locale.ENGLISH).parseDateTime(o2));
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        });
        List<MyEvent> value = null;
        for(MyEvent event: events_all)
            if(isInDateRange(event, date_range)){
                mapkey = createMapkeyMonths(event.getStartDate());
                value = data.get(mapkey);
                if(value == null) value = new ArrayList<>();
                value.add(event);
                data.put(mapkey, value);
            }
        return data;
    }

    /* Returns data in form: key example: "2013", value: list of events in this year. */
    public TreeMap<Integer, List<MyEvent>> getDataPerYears(DateRange date_range){
        int mapkey;
        TreeMap<Integer, List<MyEvent>> data = new TreeMap<>();
        List<MyEvent> value = null;
        for(MyEvent event: events_all)
            if(isInDateRange(event, date_range)){
                mapkey = createMapkeyYears(event.getStartDate());
                value = data.get(mapkey);
                if(value == null) value = new ArrayList<>();
                value.add(event);
                data.put(mapkey, value);
            }
        return data;
    }
    
    private String createMapkeyDays(Calendar c){
        return DATE_FORMAT_DAYS.withLocale(Locale.ENGLISH).print(new DateTime(c.getTime().getTime()));
    }
    
    private String createMapkeyWeeks(Calendar c){
        DateTime dt = new DateTime(c.getTime().getTime());
        return DATE_FORMAT_WEEKS.withLocale(Locale.ENGLISH).print(dt.withDayOfWeek(DateTimeConstants.MONDAY));
    }
    
    private String createMapkeyMonths(Calendar c){
        return DATE_FORMAT_MONTHS.withLocale(Locale.ENGLISH).print(new DateTime(c.getTime().getTime()));
    }
    
    private int createMapkeyYears(Calendar c){
        DateTime dt = new DateTime(c.getTime().getTime());
        return dt.getYear();
    }
    
    /* Determines if specified event is in specified date range. */
    private boolean isInDateRange(MyEvent event, DateRange date_range){
        if(date_range.isIncludedWholeCalendar()) 
            return true;
        if(date_range.getDateFrom() != null && date_range.getDateTo() != null)
            return event.getStartDate().getTime().after(date_range.getDateFrom().getTime()) && event.getStartDate().getTime().before(date_range.getDateTo().getTime());
        else if(date_range.getDateFrom() == null)
            return event.getStartDate().getTime().before(date_range.getDateTo().getTime());
        else 
            return event.getStartDate().getTime().after(date_range.getDateFrom().getTime());
    }
}

