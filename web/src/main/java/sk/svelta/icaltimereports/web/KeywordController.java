package sk.svelta.icaltimereports.web;

import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.Keyword;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import net.fortuna.ical4j.data.CalendarBuilder;
import sk.svelta.icaltimereports.ejb.CalendarFacade;
import sk.svelta.icaltimereports.entity.Calendar;

/**
 *
 * @author Jaroslav Švelta
 */
@Named
@SessionScoped
public class KeywordController implements Serializable {

    private static final long serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(KeywordController.class.getName());

    @EJB
    private CalendarFacade calFacade;

    private List<Keyword> items = new ArrayList<>();
    private ICalFile.KeywordsFilter keywordsFilter;
    private int calendarId;
    private String pattern;

   /**
     * @return list of calendars for selectOneListbox component
     */
    public List<Calendar> getSelectOneCalendars() {
        return calFacade.findAll();
    }

    public String importCalendar() {
        String result = null;
        LOG.log(Level.INFO, "calendarId: {0}", calendarId);
        LOG.log(Level.INFO, "keywordsFilter: {0}", keywordsFilter);
        Calendar selectedCalendar = calFacade.find(calendarId);
        LOG.log(Level.INFO, "calendar: {0}", selectedCalendar);

        CalendarBuilder cb = new CalendarBuilder();
        try {
            net.fortuna.ical4j.model.Calendar iCalendar = cb.build(new StringReader(selectedCalendar.getContent()));
            ICalFile iCalFile = new ICalFile(iCalendar);
            if (pattern != null && !pattern.isEmpty()) {
                getKeywordsFilter().setRegex(pattern);
            }
            items = iCalFile.getKeywordsList(getKeywordsFilter());
            result = "List";
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return result;
    }

    /**
     * Get the value of calendarId
     *
     * @return the value of calendarId
     */
    public int getCalendarId() {
        return calendarId;
    }

    /**
     * Set the value of calendarId
     *
     * @param calendarId new value of calendarId
     */
    public void setCalendarId(int calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * Get the value of items
     *
     * @return the value of items
     */
    public List<Keyword> getItems() {
        return items;
    }

    /**
     * Set the value of items
     *
     * @param items new value of items
     */
    public void setItems(List<Keyword> items) {
        this.items = items;
    }

    /**
     * Get the value of keywordsFilter
     *
     * @return the value of keywordsFilter
     */
    public ICalFile.KeywordsFilter getKeywordsFilter() {
        if (keywordsFilter == null) {
            keywordsFilter = new ICalFile.KeywordsFilter();
            keywordsFilter.setOccurrence(1);
            keywordsFilter.setWholeCalendar(true);
        }
        return keywordsFilter;
    }

    /**
     * Set the value of keywordsFilter
     *
     * @param keywordsFilter new value of keywordsFilter
     */
    public void setKeywordsFilter(ICalFile.KeywordsFilter keywordsFilter) {
        this.keywordsFilter = keywordsFilter;
    }

    /**
     * Get the value of pattern
     *
     * @return the value of pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Set the value of pattern
     *
     * @param pattern new value of pattern
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
