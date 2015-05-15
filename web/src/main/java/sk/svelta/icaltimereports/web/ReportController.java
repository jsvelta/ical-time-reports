package sk.svelta.icaltimereports.web;

import cz.krasny.icalstats.data.classes.DateRange;
import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.output.ExportConfiguration;
import cz.krasny.icalstats.data.classes.output.Exporter;
import cz.krasny.icalstats.data.classes.output.GroupBy;
import cz.krasny.icalstats.data.classes.output.OutputFormat;
import cz.krasny.icalstats.data.classes.output.Units;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import net.fortuna.ical4j.data.CalendarBuilder;
import sk.svelta.icaltimereports.entity.Calendar;
import sk.svelta.icaltimereports.web.util.JsfUtil;

/**
 * @author Jaroslav Švelta
 */
@Named
@SessionScoped
public class ReportController implements Serializable {

    private static final Logger LOG = Logger.getLogger(ReportController.class.getName());
    private static final long serialVersionUID = 1;

    @Inject
    private KeywordController keywordController;

    @Inject
    private CalendarController calendarController;

    private Settings settings;
    private int[] calendars;

    /**
     * @return array of GroupBy for selectOneListbox component
     */
    public GroupBy[] getSelectOneGroupBy() {
        return GroupBy.values();
    }

    /**
     * @return array of Units for selectOneListbox component
     */
    public Units[] getSelectOneUnits() {
        return Units.values();
    }

    /**
     * Get the value of settings
     *
     * @return the value of settings
     */
    public Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
            settings.setGroupBy(GroupBy.Events);
            settings.setUnits(Units.Days);
            settings.setWholeCalendar(true);
        }
        return settings;
    }

    /**
     * Set the value of settings
     *
     * @param settings new value of settings
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public int[] getCalendars() {
        return calendars;
    }

    public void setCalendars(int[] calendars) {
        this.calendars = calendars;
    }

    private List<ICalFile> getICalFiles() throws Exception {
        List<ICalFile> files = new ArrayList<>();
        CalendarBuilder builder = new CalendarBuilder();
        ListDataModel<Calendar> calendars = this.calendarController.getItems();
        for (int calendarNo : getCalendars()) {
            calendars.setRowIndex(calendarNo);
            URL url = new URL(calendars.getRowData().getUrl());
            ICalFile file = new ICalFile(builder.build(url.openStream()));
            file.parseWordsInEvents(true, false);
            files.add(file);
        }
        return files;
    }

    public void generate() {
        Exporter exporter = new Exporter();
        try {
            File report = exporter.export(
                    getICalFiles(),
                    keywordController.getItems(),
                    getSettings().toExportConfiguration());

            // Write report to response
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.reset();
            String contentType = Files.probeContentType(report.toPath());
            response.setContentType(contentType);
            response.setContentLengthLong(report.length());
            response.setHeader("Content-Disposition", "attachment;filename="+report.getName());

            try (OutputStream output = response.getOutputStream(); InputStream input = new FileInputStream(report)) {
                byte[] bytesBuffer = new byte[1024];
                int bytesReaded;
                while ((bytesReaded = input.read(bytesBuffer)) > 0) {
                    output.write(bytesBuffer, 0, bytesReaded);
                }
            }
            facesContext.responseComplete();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            JsfUtil.addErrorMessage(e, "Error in generating report");
        }

    }

    public static class Settings {

        private GroupBy groupBy;
        private Units units;
        private boolean wholeCalendar;
        private Date startDate;
        private Date endDate;
        private boolean eventsIncluded;
        private boolean datesIncluded;
        private boolean dayNamesIncluded;
        private boolean intervalIncluded;
        private boolean emptyRowsIncluded;

        // <editor-fold defaultstate="collapsed" desc="Getters and Setters">

        /**
         * Get the value of groupBy
         *
         * @return the value of groupBy
         */
        public GroupBy getGroupBy() {
            return groupBy;
        }

        /**
         * Set the value of groupBy
         *
         * @param groupBy new value of groupBy
         */
        public void setGroupBy(GroupBy groupBy) {
            this.groupBy = groupBy;
        }

        /**
         * Get the value of units
         *
         * @return the value of units
         */
        public Units getUnits() {
            return units;
        }

        /**
         * Set the value of units
         *
         * @param units new value of units
         */
        public void setUnits(Units units) {
            this.units = units;
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
         * Get the value of eventsIncluded
         *
         * @return the value of eventsIncluded
         */
        public boolean isEventsIncluded() {
            return eventsIncluded;
        }

        /**
         * Set the value of eventsIncluded
         *
         * @param eventsIncluded new value of eventsIncluded
         */
        public void setEventsIncluded(boolean eventsIncluded) {
            this.eventsIncluded = eventsIncluded;
        }

        /**
         * Get the value of datesIncluded
         *
         * @return the value of datesIncluded
         */
        public boolean isDatesIncluded() {
            return datesIncluded;
        }

        /**
         * Set the value of datesIncluded
         *
         * @param datesIncluded new value of datesIncluded
         */
        public void setDatesIncluded(boolean datesIncluded) {
            this.datesIncluded = datesIncluded;
        }

        /**
         * Get the value of dayNamesIncluded
         *
         * @return the value of dayNamesIncluded
         */
        public boolean isDayNamesIncluded() {
            return dayNamesIncluded;
        }

        /**
         * Set the value of dayNamesIncluded
         *
         * @param dayNamesIncluded new value of dayNamesIncluded
         */
        public void setDayNamesIncluded(boolean dayNamesIncluded) {
            this.dayNamesIncluded = dayNamesIncluded;
        }

        /**
         * Get the value of intervalIncluded
         *
         * @return the value of intervalIncluded
         */
        public boolean isIntervalIncluded() {
            return intervalIncluded;
        }

        /**
         * Set the value of intervalIncluded
         *
         * @param intervalIncluded new value of intervalIncluded
         */
        public void setIntervalIncluded(boolean intervalIncluded) {
            this.intervalIncluded = intervalIncluded;
        }

        /**
         * Get the value of emptyRowsIncluded
         *
         * @return the value of emptyRowsIncluded
         */
        public boolean isEmptyRowsIncluded() {
            return emptyRowsIncluded;
        }

        /**
         * Set the value of emptyRowsIncluded
         *
         * @param emptyRowsIncluded new value of emptyRowsIncluded
         */
        public void setEmptyRowsIncluded(boolean emptyRowsIncluded) {
            this.emptyRowsIncluded = emptyRowsIncluded;
        }

        // </editor-fold>

        @Override
        public String toString() {
            return "Settings{" + "groupBy=" + groupBy + ", units=" + units + ", wholeCalendar=" + wholeCalendar + ", startDate=" + startDate + ", endDate=" + endDate + ", eventsIncluded=" + eventsIncluded + ", datesIncluded=" + datesIncluded + ", dayNamesIncluded=" + dayNamesIncluded + ", intervalIncluded=" + intervalIncluded + ", emptyRowsIncluded=" + emptyRowsIncluded + '}';
        }

        /**
         * Creates a {@link Calendar} from {@link Date}
         * @param date to convert
         * @return new {@link Calendar} instance
         */
        private java.util.Calendar createCalendar(Date date) {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }

        /**
         * Convert data to {@link ExportConfiguration}
         * @return return {@link ExportConfiguration} instance
         */
        public ExportConfiguration toExportConfiguration() {
            ExportConfiguration config = new ExportConfiguration();
            config.setGroupBy(getGroupBy());
            config.setUnit(getUnits());
            config.setIncludeEvents(isEventsIncluded());
            config.setIncludeEventDate(isDatesIncluded());
            config.setIncludeEventDayOfWeek(isDayNamesIncluded());
            config.setIncludeEventFromTo(isIntervalIncluded());
            config.setIncludeEmptyRows(isEmptyRowsIncluded());
            config.setOutputFormat(OutputFormat.XLSX);

            if (isWholeCalendar()) {
                config.setDateRange(new DateRange(true));
            } else {
                config.setDateRange(new DateRange(
                        createCalendar(getStartDate()),
                        createCalendar(getEndDate())));
            }
            return config;
        }

    }
}
