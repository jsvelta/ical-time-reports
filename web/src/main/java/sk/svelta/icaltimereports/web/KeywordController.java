package sk.svelta.icaltimereports.web;

import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.Keyword;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.Min;
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
    private GatheringSettings gatheringSettings;

    /**
     * @return list of calendars for selectOneListbox component
     */
    public List<Calendar> getSelectOneCalendars() {
        return calFacade.findAll();
    }

    public String importCalendar() {
        String result = null;
        LOG.log(Level.INFO, "gatheringSettings: {0}", getGatheringSettings());
        Calendar selectedCalendar = calFacade.find(getGatheringSettings().getCalendarId());

        CalendarBuilder cb = new CalendarBuilder();
        try {
            net.fortuna.ical4j.model.Calendar iCalendar = cb.build(new StringReader(selectedCalendar.getContent()));
            ICalFile iCalFile = new ICalFile(iCalendar);
            items = iCalFile.getKeywordsList(getGatheringSettings().toKeywordsFilter());
            result = "List";
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
     * Get the value of gatheringSettings
     *
     * @return the value of gatheringSettings
     */
    public GatheringSettings getGatheringSettings() {
        if (gatheringSettings == null) {
            gatheringSettings = new GatheringSettings();
            gatheringSettings.setWholeCalendar(true);
            gatheringSettings.setMinOccurence(1);
        }
        return gatheringSettings;
    }

    /**
     * Set the value of gatheringSettings
     *
     * @param gatheringSettings new value of gatheringSettings
     */
    public void setGatheringSettings(GatheringSettings gatheringSettings) {
        this.gatheringSettings = gatheringSettings;
    }
    // </editor-fold>

    public static class GatheringSettings {

        private int calendarId;
        private boolean wholeCalendar;
        private Date startDate;
        private Date endDate;

        @Min(1)
        private int minOccurence;
        private String pattern;
        private boolean ignoreDiacritics;
        private boolean caseSensitive;

        // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
         * Get the value of wholeCalendar
         *
         * @return the value of wholeCalendar
         */
        public boolean isWholeCalendar() {
            return wholeCalendar;
        }

        /**
         * Set the value of wholeCalendar
         *
         * @param wholeCalendar new value of wholeCalendar
         */
        public void setWholeCalendar(boolean wholeCalendar) {
            this.wholeCalendar = wholeCalendar;
        }

        /**
         * Get the value of startDate
         *
         * @return the value of startDate
         */
        public Date getStartDate() {
            return startDate;
        }

        /**
         * Set the value of startDate
         *
         * @param startDate new value of startDate
         */
        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        /**
         * Get the value of endDate
         *
         * @return the value of endDate
         */
        public Date getEndDate() {
            return endDate;
        }

        /**
         * Set the value of endDate
         *
         * @param endDate new value of endDate
         */
        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        /**
         * Get the value of minOccurence
         *
         * @return the value of minOccurence
         */
        public int getMinOccurence() {
            return minOccurence;
        }

        /**
         * Set the value of minOccurence
         *
         * @param minOccurence new value of minOccurence
         */
        public void setMinOccurence(int minOccurence) {
            this.minOccurence = minOccurence;
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

        /**
         * Get the value of ignoreDiacritics
         *
         * @return the value of ignoreDiacritics
         */
        public boolean isIgnoreDiacritics() {
            return ignoreDiacritics;
        }

        /**
         * Set the value of ignoreDiacritics
         *
         * @param ignoreDiacritics new value of ignoreDiacritics
         */
        public void setIgnoreDiacritics(boolean ignoreDiacritics) {
            this.ignoreDiacritics = ignoreDiacritics;
        }

        /**
         * Get the value of caseSensitive
         *
         * @return the value of caseSensitive
         */
        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        /**
         * Set the value of caseSensitive
         *
         * @param caseSensitive new value of caseSensitive
         */
        public void setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }
        // </editor-fold>

        @Override
        public String toString() {
            return "GatheringSettings{" + "calendarId=" + calendarId + ", wholeCalendar=" + wholeCalendar + ", startDate=" + startDate + ", endDate=" + endDate + ", minOccurence=" + minOccurence + ", pattern=" + pattern + ", ignoreDiacritics=" + ignoreDiacritics + ", caseSensitive=" + caseSensitive + '}';
        }

        /**
         * Convert to ICalFile.KeywordsFilter
         * @return return ICalFile.KeywordsFilter instance
         */
        public ICalFile.KeywordsFilter toKeywordsFilter() {
            ICalFile.KeywordsFilter filter = new ICalFile.KeywordsFilter();
            filter.setWholeCalendar(isWholeCalendar());
            if (!isWholeCalendar()) {
                java.util.Calendar startCalendar = java.util.Calendar.getInstance();
                startCalendar.setTime(getStartDate());
                filter.setStartDate(startCalendar);

                java.util.Calendar endCalendar = java.util.Calendar.getInstance();
                endCalendar.setTime(getEndDate());
                filter.setEndDate(endCalendar);
            }
            filter.setOccurrence(getMinOccurence());
            String pattern = getPattern();
            if (pattern != null && !pattern.isEmpty()) {
                filter.setRegex(getPattern());
            }
            filter.setRemoveDiacritics(isIgnoreDiacritics());
            filter.setCaseSensitive(isCaseSensitive());
            return filter;
        }

    }

}