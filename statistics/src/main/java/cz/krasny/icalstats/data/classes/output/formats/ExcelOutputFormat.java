package cz.krasny.icalstats.data.classes.output.formats;

import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.Keyword;
import cz.krasny.icalstats.data.classes.MyEvent;
import cz.krasny.icalstats.data.classes.output.AOutputFormat;
import cz.krasny.icalstats.data.classes.output.DataTransformer;
import cz.krasny.icalstats.data.classes.output.ExportConfiguration;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/***
 * Class generating statistics in Excel workbook.
 * @author Tomas Krasny
 */
public class ExcelOutputFormat extends AOutputFormat{

    private static final DateTimeFormatter DATE_FORMAT_WEEKS = DateTimeFormat.forPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_FORMAT_MONTHS = DateTimeFormat.forPattern("MMMM yyyy");
    private static final DateTimeFormatter DATE_FORMAT_DAYS = DateTimeFormat.forPattern("dd.MM.yyyy");
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormat.forPattern("MMM yyyy");
    private static final DateTimeFormatter MONTH = DateTimeFormat.forPattern("MMMM");
    private static final int START_ROW_CONFIGURATION_SUMMARY = 1;
    private static final int START_ROW_STATISTICS = 0;

    private ExportConfiguration ec = null;
    private DataTransformer data_t = null;
    private List<String> keywords = null;
    private Workbook workbook = null;
    private Styles styles = null;
    private Sheet sheet = null;
    private short pointerX = 0;
    private short pointerY = 0;

    private void generatePerIndividualEvents(){
        sheet = workbook.createSheet("Statistics per individual events");
        sheet.createFreezePane(0, 2);
        writeConfigurationSummary();
        writePerIndividualEventHeader();
        writePerIndividualEventData();
    }
    private void writePerIndividualEventHeader(){
        pointerX = START_ROW_STATISTICS;
        pointerY = 0;
        addCell("date", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        addCell("event subject", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        sheet.autoSizeColumn(pointerY-1);
        if(ec.isIncludeEventFromTo()){
            addCell("from", pointerX, pointerY, styles.getStyleHeader());
            addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            addCell("to", pointerX, pointerY, styles.getStyleHeader());
            addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        }
        if(ec.isIncludeEventDayOfWeek()){
            addCell("day", pointerX, pointerY, styles.getStyleHeader());
            addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        }
        addCell("sum", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        for(int i = 0 ; i < keywords.size() ; i++){
            addCell(keywords.get(i), pointerX, pointerY, i == 0 ? styles.getStyleHeaderFirstKeyword() : styles.getStyleHeaderKeyword());   //date
            addCell(0, pointerX+1, pointerY++, i == 0 ? styles.getStyleTotalSummaryRowLeftBold(): styles.getStyleTotalSummaryRow());
            sheet.autoSizeColumn(pointerY-1);
        }
        addCell("others", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        addCell("total", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        //move due to summary yellow row
        pointerX++;
    }
    private void writePerIndividualEventData(){
        pointerX++;
        pointerY = 0;
        boolean contains_keyword;
        double duration = 0;
        double tmp = 0;
        List<MyEvent> data = data_t.getDataPerIndividualEvents(ec.getDateRange());
        for (MyEvent event : data) {
            //date
            addCell(event.getStartDate(), pointerX, pointerY++, styles.getStyleSummaryRowDate(pointerX));
            //summary
            addCell(event.getSummary(), pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));
            if(ec.isIncludeEventFromTo()){
                //from
                addCell(event.getStartDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));
                //to
                addCell(event.getEndDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));
            }
            if(ec.isIncludeEventDayOfWeek()){
                //day
                addCell(event.getStartDateWithSpecificFormat("EE"), pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));
            }
            //SUM
            contains_keyword = containsKeyword(event, keywords);
            duration = getDuration(event, "");
            if(contains_keyword){
                addCell(duration, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
            }
            else
                addCell(0, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
            for(int j = 0; j < keywords.size() ; j++){
                //duration
                tmp = getDuration(event, keywords.get(j));
                addCell(tmp, pointerX, pointerY++,  j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
            }
            //other
            if(contains_keyword)
                addCell(0, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
            else {
                addCell(duration, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
            }
            //total
            addCell(duration, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
            setCellValue(duration + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
            pointerX++;
            pointerY = 0;
        }
    }

    private void generatePerDays() throws Exception{
        sheet = workbook.createSheet("Statistics per days");
        sheet.createFreezePane(0, 2);
        writeConfigurationSummary();
        writePerDayHeader();
        writePerDayData();
    }
    private void writePerDayHeader() {
        pointerX = START_ROW_STATISTICS;
        pointerY = 0;
        addCell("date", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        if(ec.isIncludeEvents()){
            addCell("event subject", pointerX, pointerY, styles.getStyleHeader());
            addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            if(ec.isIncludeEventFromTo()){
                addCell("from", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
                addCell("to", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
            if(ec.isIncludeEventDayOfWeek()){
                addCell("day", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
        }
        addCell("sum", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        for(int i = 0 ; i < keywords.size() ; i++){
            addCell(keywords.get(i), pointerX, pointerY, i == 0 ? styles.getStyleHeaderFirstKeyword() : styles.getStyleHeaderKeyword()); //date
            addCell(0, pointerX+1, pointerY++, i == 0 ? styles.getStyleTotalSummaryRowLeftBold() : styles.getStyleTotalSummaryRow());
            sheet.autoSizeColumn(pointerY-1);
        }
        addCell("others", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        addCell("total", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        //move due to summary yellow row
        pointerX++;
    }
    private void writePerDayData() throws Exception {
        pointerX++;
        pointerY = 0;
        double duration_events = 0;
        double duration_events_other = 0;
        double tmp = 0;
        List<MyEvent> events = new ArrayList<>();
        List<MyEvent> events_other =  new ArrayList<>();
        Calendar date = Calendar.getInstance();
        TreeMap<String, List<MyEvent>> data = data_t.getDataPerDays(ec.getDateRange());
        if(ec.isIncludeEmptyRows()){
            includeEmptyRowsDays(data);
        }
        for(Map.Entry<String,List<MyEvent>> entry : data.entrySet()) {
            events.clear();
            events_other.clear();
            splitEvents(events, events_other, entry.getValue());
            date.setTime(DATE_FORMAT_DAYS.parseDateTime(entry.getKey()).toDate());
            //date
            addCell(date, pointerX, pointerY++, styles.getStyleSummaryRowDate(pointerX));
            if(ec.isIncludeEvents()){
                addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//subject
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//from
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//to
                if(ec.isIncludeEventDayOfWeek()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY ROW PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
                pointerY = 1;
                for (MyEvent event : events) {
                    addCell(event.getSummary(), pointerX, pointerY++, null);
                    if (ec.isIncludeEventFromTo()) {
                        //from
                        addCell(event.getStartDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign());
                        //to
                        addCell(event.getEndDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign());
                    }
                    if (ec.isIncludeEventDayOfWeek()) {
                        //day
                        addCell(event.getStartDateWithSpecificFormat("EE"), pointerX, pointerY++, null);
                    }
                    addCell(getDuration(event, ""), pointerX, pointerY++, styles.getStyleFirstKeyword());
                    for (int j = 0; j < keywords.size(); j++) {
                        addCell(getDuration(event, keywords.get(j)), pointerX, pointerY++, j == 0 ? styles.getStyleFirstKeyword() : null);
                    }
                    pointerX++;
                    pointerY = 1;
                }
            }
            else{
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
            }
            pointerY = 0;
        }
    }

    private void generatePerWeeks() throws Exception{
        sheet = workbook.createSheet("Statistics per weeks");
        sheet.createFreezePane(0, 2);
        writeConfigurationSummary();
        writePerWeekHeader();
        writePerWeekData();
    }
    private void writePerWeekHeader(){
        pointerX = START_ROW_STATISTICS;
        pointerY = 0;
        addCell("week from", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        addCell("week to", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        if(ec.isIncludeEvents()){
            addCell("event subject", pointerX, pointerY, styles.getStyleHeader());
            addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            if(ec.isIncludeEventDate()){
                addCell("date",pointerX,pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
            if(ec.isIncludeEventFromTo()){
                addCell("from", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
                addCell("to", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
            if(ec.isIncludeEventDayOfWeek()){
                addCell("day", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
        }
        addCell("sum", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        for(int i = 0 ; i < keywords.size() ; i++){
            addCell(keywords.get(i), pointerX, pointerY, i == 0 ? styles.getStyleHeaderFirstKeyword() : styles.getStyleHeaderKeyword());
            addCell(0, pointerX+1, pointerY++, i == 0 ? styles.getStyleTotalSummaryRowLeftBold() : styles.getStyleTotalSummaryRow());
            sheet.autoSizeColumn(pointerY-1);
        }
        addCell("others", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        addCell("total", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        //move due to summary yellow row
        pointerX++;
    }
    private void writePerWeekData() throws Exception{
        pointerX++;
        pointerY = 0;
        double duration_events = 0;
        double duration_events_other = 0;
        double tmp = 0;
        List<MyEvent> events = new ArrayList<>();
        List<MyEvent> events_other =  new ArrayList<>();
        Calendar date = null;
        TreeMap<String, List<MyEvent>> data = data_t.getDataPerWeeks(ec.getDateRange());
        if(ec.isIncludeEmptyRows())
            includeEmptyRowsWeeks(data);
        for(Map.Entry<String,List<MyEvent>> entry : data.entrySet()) {
            events.clear();
            events_other.clear();
            splitEvents(events, events_other, entry.getValue());
            date = DATE_FORMAT_DAYS.parseDateTime(entry.getKey()).toCalendar(Locale.ENGLISH);
            addCell(date, pointerX, pointerY++, styles.getStyleSummaryRowDate(pointerX)); //week from
            date.add(Calendar.DATE, 6);
            addCell(date, pointerX, pointerY++, styles.getStyleSummaryRowDate(pointerX)); //week to
            if(ec.isIncludeEvents()){
                addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));
                if(ec.isIncludeEventDate()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//from
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//to
                if(ec.isIncludeEventDayOfWeek()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//day
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY ROW PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
                pointerY = 2;
                for (MyEvent event : events) {
                    addCell(event.getSummary(), pointerX, pointerY++, null); //subject
                    if (ec.isIncludeEventDate()) {
                        addCell(event.getStartDate(), pointerX, pointerY++, styles.getStyleDate()); //start date
                    }
                    if (ec.isIncludeEventFromTo()) {
                        addCell(event.getStartDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign()); //from
                        addCell(event.getEndDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign()); //to
                    }
                    if (ec.isIncludeEventDayOfWeek()) {
                        addCell(event.getStartDateWithSpecificFormat("EE"), pointerX, pointerY++, null);
                    }
                    addCell(getDuration(event, ""), pointerX, pointerY++, styles.getStyleFirstKeyword());
                    for (int j = 0; j < keywords.size(); j++) {
                        addCell(getDuration(event, keywords.get(j)), pointerX, pointerY++, j == 0 ? styles.getStyleFirstKeyword() : null);
                    }
                    pointerX++;
                    pointerY = 2;
                }
            }
            else{
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
            }
            pointerY = 0;
        }
    }


    private void generatePerMonths() throws Exception{
        sheet = workbook.createSheet("Statistics per months");
        sheet.createFreezePane(0, 2);
        writeConfigurationSummary();
        writePerMonthHeader();
        writePerMonthData();
    }
    private void writePerMonthHeader(){
        pointerX = START_ROW_STATISTICS;
        pointerY = 0;
        addCell("month", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        addCell("year", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        if(ec.isIncludeEvents()){
            addCell("event subject", pointerX, pointerY, styles.getStyleHeader());
            addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            if(ec.isIncludeEventDate()){
                addCell("date",pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
            if(ec.isIncludeEventFromTo()){
                addCell("from", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
                addCell("to", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
            if(ec.isIncludeEventDayOfWeek()){
                addCell("day", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
        }
        addCell("sum", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        for(int i = 0 ; i < keywords.size() ; i++){
            addCell(keywords.get(i), pointerX, pointerY, i == 0 ? styles.getStyleHeaderFirstKeyword() : styles.getStyleHeaderKeyword());   //date
            addCell(0, pointerX+1, pointerY++, i == 0 ? styles.getStyleTotalSummaryRowLeftBold() : styles.getStyleTotalSummaryRow());
            sheet.autoSizeColumn(pointerY-1);
        }
        addCell("others", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        addCell("total", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        //move due to summary yellow row
        pointerX++;
    }
    private void writePerMonthData() throws Exception{
        pointerX++;
        pointerY = 0;
        double duration_events = 0;
        double tmp = 0;
        double duration_events_other = 0;
        List<MyEvent> events = new ArrayList<>();
        List<MyEvent> events_other =  new ArrayList<>();
        Calendar date = Calendar.getInstance();
        TreeMap<String, List<MyEvent>> data = data_t.getDataPerMonths(ec.getDateRange());
        if(ec.isIncludeEmptyRows())
            includeEmptyRowsMonths(data);
        for(Map.Entry<String,List<MyEvent>> entry : data.entrySet()) {
            events.clear();
            events_other.clear();
            splitEvents(events, events_other, entry.getValue());
            date.setTime(MONTH_YEAR.withLocale(Locale.ENGLISH).parseDateTime(entry.getKey()).toDate());
            addCell(MONTH.withLocale(Locale.ENGLISH).print(date.getTime().getTime()), pointerX, pointerY++, styles.getStyleSummaryRow(pointerX)); //month
            addCell(date.get(Calendar.YEAR), pointerX, pointerY++, styles.getStyleSummaryRow(pointerX)); //year
            if(ec.isIncludeEvents()){
                addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//subject
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//from
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//to
                if(ec.isIncludeEventDayOfWeek()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));  //day
                if(ec.isIncludeEventDate()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX)); //event day
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
                pointerY = 2;
                for (MyEvent event : events) {
                    addCell(event.getSummary(), pointerX, pointerY++, null);
                    if (ec.isIncludeEventDate()) {
                        addCell(event.getStartDate(), pointerX, pointerY++, styles.getStyleDate()); //start date
                    }
                    if (ec.isIncludeEventFromTo()) {
                        addCell(event.getStartDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign());
                        addCell(event.getStartDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign());
                    }
                    if (ec.isIncludeEventDayOfWeek()) {
                        addCell(event.getStartDateWithSpecificFormat("EE"), pointerX, pointerY++, null);
                    }
                    addCell(getDuration(event, ""), pointerX, pointerY++, styles.getStyleFirstKeyword());
                    for (int j = 0; j < keywords.size(); j++) {
                        addCell(getDuration(event, keywords.get(j)), pointerX, pointerY++, j == 0 ? styles.getStyleFirstKeyword() : null);
                    }
                    pointerX++;
                    pointerY = 2;
                }
            }
            else{
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
            }
            pointerY = 0;
        }
    }

    private void generatePerYears(){
        sheet = workbook.createSheet("Statistics per years");
        sheet.createFreezePane(0, 2);
        writeConfigurationSummary();
        writePerYearHeader();
        writePerYearData();
    }
    private void writePerYearHeader(){
        pointerX = START_ROW_STATISTICS;
        pointerY = 0;
        addCell("year", pointerX, pointerY, styles.getStyleHeader());
        addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
        if(ec.isIncludeEvents()){
            addCell("event subject", pointerX, pointerY, styles.getStyleHeader());
            addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            if(ec.isIncludeEventDate()){
                addCell("date",pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
            if(ec.isIncludeEventFromTo()){
                addCell("from", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
                addCell("to", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
            if(ec.isIncludeEventDayOfWeek()){
                addCell("day", pointerX, pointerY, styles.getStyleHeader());
                addCell("", pointerX+1, pointerY++, styles.getStyleTotalSummaryRow());
            }
        }
        addCell("sum", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        for(int i = 0 ; i < keywords.size() ; i++){
            addCell(keywords.get(i), pointerX, pointerY, i == 0 ? styles.getStyleHeaderFirstKeyword() : styles.getStyleHeaderKeyword());   //date
            addCell(0, pointerX+1, pointerY++, i == 0 ? styles.getStyleTotalSummaryRowLeftBold() : styles.getStyleTotalSummaryRow());
            sheet.autoSizeColumn(pointerY-1);
        }
        addCell("others", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        addCell("total", pointerX, pointerY, styles.getStyleHeaderLeftBold());
        addCell(0, pointerX+1, pointerY++, styles.getStyleTotalSummaryRowLeftBold());
        //move due to summary yellow row
        pointerX++;
    }
    private void writePerYearData(){
        pointerX++;
        pointerY = 0;
        double duration_events = 0;
        double tmp = 0;
        double duration_events_other = 0;
        List<MyEvent> events = new ArrayList<>();
        List<MyEvent> events_other =  new ArrayList<>();
        TreeMap<Integer, List<MyEvent>> data = data_t.getDataPerYears(ec.getDateRange());
        if(ec.isIncludeEmptyRows())
            includeEmptyRowsYears(data);
        for(Map.Entry<Integer,List<MyEvent>> entry : data.entrySet()) {
            events.clear();
            events_other.clear();
            splitEvents(events, events_other, entry.getValue());
            addCell(entry.getKey(), pointerX, pointerY++, styles.getStyleSummaryRow(pointerX)); //year
            if(ec.isIncludeEvents()){
                addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//subject
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//from
                if(ec.isIncludeEventFromTo()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));//to
                if(ec.isIncludeEventDayOfWeek()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX));  //day
                if(ec.isIncludeEventDate()) addCell("", pointerX, pointerY++, styles.getStyleSummaryRow(pointerX)); //event day
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
                pointerY = 1;
                for (MyEvent event : events) {
                    addCell(event.getSummary(), pointerX, pointerY++, null);
                    if (ec.isIncludeEventDate()) {
                        addCell(event.getStartDate(), pointerX, pointerY++, styles.getStyleDate()); //start date
                    }
                    if (ec.isIncludeEventFromTo()) {
                        addCell(event.getStartDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign());
                        addCell(event.getStartDateWithSpecificFormat("HH:mm"), pointerX, pointerY++, styles.getStyleTimeRightAlign());
                    }
                    if (ec.isIncludeEventDayOfWeek()) {
                        addCell(event.getStartDateWithSpecificFormat("EE"), pointerX, pointerY++, null);
                    }
                    addCell(getDuration(event, ""), pointerX, pointerY++, styles.getStyleFirstKeyword());
                    for (int j = 0; j < keywords.size(); j++) {
                        addCell(getDuration(event, keywords.get(j)), pointerX, pointerY++, j == 0 ? styles.getStyleFirstKeyword() : null);
                    }
                    pointerX++;
                    pointerY = 1;
                }
            }
            else{
                //SUMMARY PER EVENTS
                duration_events = getDuration(events, "");
                addCell(duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //SUMMARY PER KEYWORDS
                for(int j = 0 ; j < keywords.size() ; j++){
                    tmp = getDuration(events, keywords.get(j));
                    addCell(tmp, pointerX, pointerY++, j != 0 ? styles.getStyleSummaryRow(pointerX) : styles.getStyleSummaryRowFirstKeyword(pointerX));
                    setCellValue(tmp + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                }
                //SUMMARY PER OTHER
                duration_events_other = getDuration(events_other, "");
                addCell(duration_events_other, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                //TOTAL
                addCell(duration_events_other + duration_events, pointerX, pointerY++, styles.getStyleSummaryRowFirstKeyword(pointerX));
                setCellValue(duration_events_other + duration_events + getCellNumericValue(1, pointerY-1), 1, pointerY-1);
                pointerX++;
            }
            pointerY = 0;
        }
    }


    private void writeConfigurationSummary(){
        pointerX = START_ROW_CONFIGURATION_SUMMARY;
        pointerY = 0;
        sheet = workbook.createSheet("Configuration");
        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Configuration summary", pointerX++, pointerY, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        pointerX++;

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Group by:", pointerX, pointerY, null);
        addCell(ec.getGroupBy().toString(), pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Unit:", pointerX, pointerY, null);
        addCell(ec.getUnit().toString(), pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Include event's:", pointerX, pointerY, null);
        addCell(ec.isIncludeEvents()?"Yes":"No", pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Include event's day of week:", pointerX, pointerY, null);
        addCell(ec.isIncludeEventDayOfWeek()?"Yes":"No", pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Include event's date:", pointerX, pointerY, null);
        addCell(ec.isIncludeEventDate()?"Yes":"No", pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Include event's from-to:", pointerX, pointerY, null);
        addCell(ec.isIncludeEventFromTo()?"Yes":"No", pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Include empty rows:", pointerX, pointerY, null);
        addCell(ec.isIncludeEmptyRows()?"Yes":"No", pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Output format:", pointerX,pointerY, null);
        addCell(ec.getOutputFormat().toString(), pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Include whole calendar:", pointerX, pointerY, null);
        addCell(ec.getDateRange().isIncludedWholeCalendar()?"Yes":"No", pointerX++, pointerY+4, null);

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Date from:", pointerX, pointerY, null);
        addCell(ec.getDateRange().getDateFrom(), pointerX++, pointerY+4, styles.getStyleDate());

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Date to:", pointerX, pointerY, null);
        addCell(ec.getDateRange().getDateTo(), pointerX++, pointerY+4, styles.getStyleDate());

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Remove diacritics:", pointerX, pointerY, null);
        addCell(ec.isRemoveDiacritics()?"Yes":"No", pointerX++, pointerY+4, styles.getStyleDate());

        sheet.addMergedRegion(new CellRangeAddress(pointerX, pointerX, pointerY, pointerY+3));
        addCell("Case sensitive:", pointerX, pointerY, null);
        addCell(ec.isCaseSensitive()?"Yes":"No", pointerX++, pointerY+4, styles.getStyleDate());

        sheet = workbook.getSheetAt(0);
    }
    private double getDuration(MyEvent event, String keyword){
        double dur = 0;
        if(!event.getWords().contains(keyword) && !keyword.equals(""))
            return dur;
        dur = event.getDuration(ec.getUnit());
        return dur;
    }
    private double getDuration(List<MyEvent> events, String keyword){
        double dur = 0;
        if(events == null)
            return dur;
        for(MyEvent event: events){
            dur += getDuration(event, keyword);
        }
        return dur;
    }
    private void addCell(Object content, int rowIndex, int columnIndex, CellStyle style){
        try{
            Row row = sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);
            Cell cell = row.createCell(columnIndex);
            if(content == null)
                return;
            if(content instanceof String)
                cell.setCellValue(content.toString());
            else if(content instanceof Integer)
                cell.setCellValue((int) content);
            else if(content instanceof Double)
                cell.setCellValue((double) content);
            else if(content instanceof Calendar)
                cell.setCellValue((Calendar) content);
            else
                cell.setCellValue(content.toString());
            if(style != null)
                cell.setCellStyle(style);
            sheet.autoSizeColumn(columnIndex);
        }
        catch(Exception ex){
            String err_text = "";
            cz.krasny.icalstats.data.classes.Logger.appendException(ex);
            err_text += ex.getMessage() + System.getProperty("line.separator") + System.getProperty("line.separator");
            err_text += "See log.txt for more details.";
            System.out.println(err_text);
        }
    }
    private void setCellValue(Object content, int rowIndex, int columnIndex){
        try{
            Cell cell = sheet.getRow(rowIndex).getCell(columnIndex);
            if(content instanceof String)
                cell.setCellValue(content.toString());
            else if(content instanceof Integer)
                cell.setCellValue((int) content);
            else if(content instanceof Double)
                cell.setCellValue((double) content);
            else if(content instanceof Calendar)
                cell.setCellValue((Calendar) content);
            else
                cell.setCellValue(content.toString());
            sheet.autoSizeColumn(columnIndex);
        }
        catch(Exception ex){
            String err_text = "";
            cz.krasny.icalstats.data.classes.Logger.appendException(ex);
            err_text += ex.getMessage() + System.getProperty("line.separator") + System.getProperty("line.separator");
            err_text += "See log.txt for more details.";
            System.out.println(err_text);
        }
    }
    private double getCellNumericValue(int rowIndex, int columnIndex){
        return sheet.getRow(rowIndex).getCell(columnIndex).getNumericCellValue();
    }
    private void splitEvents(List<MyEvent> with_kw, List<MyEvent> without_kw, List<MyEvent> all_events){
        for(MyEvent event: all_events){
            if(containsKeyword(event, keywords))
                with_kw.add(event);
            else
                without_kw.add(event);
        }
    }
    private boolean containsKeyword(MyEvent event, List<String> keywords){
        List<String> words = event.getWords();
        if (words == null) {
            return false;
        }
        for(String keyword: keywords)
            if(words.contains(keyword))
                return true;
        return false;
    }

    private void includeEmptyRowsDays(TreeMap<String, List<MyEvent>> data) {
        //key: "3.4.2013"
        String first_key = data.firstKey();
        String last_key = data.lastKey();
        String mapkey = "";
        DateTime date = DATE_FORMAT_DAYS.withLocale(Locale.ENGLISH).parseDateTime(first_key);
        while(true){
            if(first_key.equals(last_key)) return;
            date = date.plusDays(1);
            mapkey = createMapkeyDays(date.toCalendar(Locale.ENGLISH));
            if(!data.containsKey(mapkey))
                data.put(mapkey, new ArrayList<MyEvent>());
            first_key = mapkey;
        }
    }
    private void includeEmptyRowsWeeks(TreeMap<String, List<MyEvent>> data) {
        //key: "3.4.2013"
        String first_key = data.firstKey();
        String last_key = data.lastKey();
        String mapkey = "";
        DateTime date = DATE_FORMAT_WEEKS.withLocale(Locale.ENGLISH).parseDateTime(first_key);
        while(true){
            if(first_key.equals(last_key)) return;
            date = date.plusDays(1);
            mapkey = createMapkeyWeeks(date.toCalendar(Locale.ENGLISH));
            if(!data.containsKey(mapkey))
                data.put(mapkey, new ArrayList<MyEvent>());
            first_key = mapkey;
        }
    }
    private void includeEmptyRowsMonths(TreeMap<String, List<MyEvent>> data) {
        //key: "April 2013"
        String first_key = data.firstKey();
        String last_key = data.lastKey();
        String mapkey = "";
        DateTime date = DATE_FORMAT_MONTHS.withLocale(Locale.ENGLISH).parseDateTime(first_key);
        while(true){
            if(first_key.equals(last_key)) return;
            date = date.plusDays(1);
            mapkey = createMapkeyMonths(date.toCalendar(Locale.ENGLISH));
            if(!data.containsKey(mapkey))
                data.put(mapkey, new ArrayList<MyEvent>());
            first_key = mapkey;
        }
    }
    private void includeEmptyRowsYears(TreeMap<Integer, List<MyEvent>> data) {
        //key: "2013"
        int first_key = data.firstKey();
        int last_key = data.lastKey();
        int mapkey;
        while(true){
            if(first_key == last_key) return;
            first_key++;
            mapkey = first_key;
            if(!data.containsKey(mapkey))
                data.put(mapkey, new ArrayList<MyEvent>());
            first_key = mapkey;
        }
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

    private void generateStatistic() throws Exception {
        switch(ec.getGroupBy()){
            case Events: generatePerIndividualEvents(); break;
            case Days: generatePerDays(); break;
            case Weeks: generatePerWeeks(); break;
            case Months: generatePerMonths(); break;
            case Years: generatePerYears(); break;
            default: throw new IllegalArgumentException("Unknow granularity: "+ec.getGroupBy());
        }
    }
    private File generateFile() throws Exception{
        String filename = getDefaultFileName();
        File file = null;
        switch(ec.getOutputFormat()){
            case XLS: workbook = new HSSFWorkbook(); filename += ".xls"; break;
            case XLSX: workbook = new XSSFWorkbook(); filename += ".xlsx"; break;
            default: throw new IllegalArgumentException("Unknow output format: "+ec.getOutputFormat());
        }
        styles = new Styles(workbook);
        file = new File(filename);
        generateStatistic();

        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.close();
        return file;
    }

    /* Generates statistics. */
    @Override
    public File createStatistic(List<ICalFile> ical_files, List<Keyword> keywords, ExportConfiguration ec) throws Exception {
        this.ec = ec;
        this.keywords = new ArrayList<>();
        // Occurrence is not important
        for(Keyword k: keywords)
            this.keywords.add(k.getKeyword());
        data_t = new DataTransformer(ical_files);
        File file = null;
        file = generateFile();
        return file;
    }

}

/***
 * Class representing styles for Excel workbook.
 * @author Tomas Krasny
 */
class Styles{

    private Workbook wb = null;

    private CellStyle style_total_summary_row;
    private CellStyle style_total_summary_row_left_bold;
    private CellStyle style_date;
    private CellStyle style_first_keyword;
    private CellStyle style_header;
    private CellStyle style_header_header;
    private CellStyle style_time_right_align;
    private CellStyle style_summary_row_even;
    private CellStyle style_summary_row_odd;
    private CellStyle style_first_keyword_summary_even;
    private CellStyle style_first_keyword_summary_odd;
    private CellStyle style_summary_row_date_even;
    private CellStyle style_summary_row_date_odd;
    private CellStyle style_header_first_keyword;
    private CellStyle style_header_left_bold;
    private CellStyle style_summary_row_bold_even;
    private CellStyle style_summary_row_bold_odd;
    private boolean row_a = true;
    private int actual_row = 0;

    public Styles(Workbook workbook){
        wb = workbook;
    }

    public CellStyle getStyleSummaryRow(int row) {
        if(row != actual_row){
            row_a = !row_a;
            actual_row = row;
        }
        if(row_a)
            return getStyleSummaryRowA();
        else
            return getStyleSummaryRowB();
    }
    private CellStyle getStyleSummaryRowA(){
        if(style_summary_row_even == null){
            style_summary_row_even = wb.createCellStyle();
            style_summary_row_even.setBorderTop(CellStyle.BORDER_THIN);
            style_summary_row_even.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style_summary_row_even.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }
        return style_summary_row_even;
    }
    private CellStyle getStyleSummaryRowB(){
        if(style_summary_row_odd == null){
            style_summary_row_odd = wb.createCellStyle();
            style_summary_row_odd.setBorderTop(CellStyle.BORDER_THIN);
            style_summary_row_odd.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_summary_row_odd.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }
        return style_summary_row_odd;
    }

    public CellStyle getStyleSummaryRowDate(int row){
        if(row != actual_row){
            row_a = !row_a;
            actual_row = row;
        }
        if(row_a)
            return getStyleSummaryRowDateA();
        else
            return getStyleSummaryRowDateB();
    }
    private CellStyle getStyleSummaryRowDateA(){
        if(style_summary_row_date_even == null){
            style_summary_row_date_even = wb.createCellStyle();
            CreationHelper ch = wb.getCreationHelper();
            style_summary_row_date_even.setBorderTop(CellStyle.BORDER_THIN);
            style_summary_row_date_even.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style_summary_row_date_even.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_summary_row_date_even.setDataFormat(ch.createDataFormat().getFormat("dd.MM.yyyy"));
        }
        return style_summary_row_date_even;
    }
    private CellStyle getStyleSummaryRowDateB(){
        if(style_summary_row_date_odd == null){
            style_summary_row_date_odd = wb.createCellStyle();
            CreationHelper ch = wb.getCreationHelper();
            style_summary_row_date_odd.setBorderTop(CellStyle.BORDER_THIN);
            style_summary_row_date_odd.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_summary_row_date_odd.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_summary_row_date_odd.setDataFormat(ch.createDataFormat().getFormat("dd.MM.yyyy"));
        }
        return style_summary_row_date_odd;
    }

    public CellStyle getStyleSummaryRowFirstKeyword(int row) {
        if(row != actual_row){
            row_a = !row_a;
            actual_row = row;
        }
        if(row_a)
            return getStyleSummaryRowFirstKeywordB();
        else
            return getStyleSummaryRowFirstKeywordA();
    }
    private CellStyle getStyleSummaryRowFirstKeywordA(){
        if(style_first_keyword_summary_odd == null){
            style_first_keyword_summary_odd = wb.createCellStyle();
            style_first_keyword_summary_odd.setBorderTop(CellStyle.BORDER_THIN);
            style_first_keyword_summary_odd.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_first_keyword_summary_odd.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_first_keyword_summary_odd.setBorderLeft(CellStyle.BORDER_THICK);
            style_first_keyword_summary_odd.setLeftBorderColor(IndexedColors.BLACK.index);
        }
        return style_first_keyword_summary_odd;
    }
    private CellStyle getStyleSummaryRowFirstKeywordB(){
        if(style_first_keyword_summary_even == null){
            style_first_keyword_summary_even = wb.createCellStyle();
            style_first_keyword_summary_even.setBorderTop(CellStyle.BORDER_THIN);
            style_first_keyword_summary_even.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style_first_keyword_summary_even.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_first_keyword_summary_even.setBorderLeft(CellStyle.BORDER_THICK);
            style_first_keyword_summary_even.setLeftBorderColor(IndexedColors.BLACK.index);
        }
        return style_first_keyword_summary_even;
    }

    public CellStyle getStyleSummaryRowBoldBorder(int row){
        if(row != actual_row){
            row_a = !row_a;
            actual_row = row;
        }
        if(row_a)
            return getStyleSummaryRowBoldBorderA();
        else
            return getStyleSummaryRowBoldBorderB();
    }
    private CellStyle getStyleSummaryRowBoldBorderA(){
        if(style_summary_row_bold_even == null){
            style_summary_row_bold_even = wb.createCellStyle();
            Font f = wb.createFont();
            f.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style_summary_row_bold_even.setFont(f);
            style_summary_row_bold_even.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style_summary_row_bold_even.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_summary_row_bold_even.setBorderTop(CellStyle.BORDER_THIN);
            style_summary_row_bold_even.setBorderLeft(CellStyle.BORDER_THIN);
            style_summary_row_bold_even.setBorderRight(CellStyle.BORDER_THIN);
            style_summary_row_bold_even.setBorderBottom(CellStyle.BORDER_THIN);
        }
        return style_summary_row_bold_even;
    }
    private CellStyle getStyleSummaryRowBoldBorderB(){
        if(style_summary_row_bold_odd == null){
            style_summary_row_bold_odd = wb.createCellStyle();
            Font f = wb.createFont();
            f.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style_summary_row_bold_odd.setFont(f);
            style_summary_row_bold_odd.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_summary_row_bold_odd.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_summary_row_bold_odd.setBorderTop(CellStyle.BORDER_THIN);
            style_summary_row_bold_odd.setBorderLeft(CellStyle.BORDER_THIN);
            style_summary_row_bold_odd.setBorderRight(CellStyle.BORDER_THIN);
            style_summary_row_bold_odd.setBorderBottom(CellStyle.BORDER_THIN);
        }
        return style_summary_row_bold_odd;
    }

    public CellStyle getStyleFirstKeyword() {
       if(style_first_keyword == null){
            style_first_keyword = wb.createCellStyle();
            style_first_keyword.setBorderLeft(CellStyle.BORDER_THICK);
            style_first_keyword.setLeftBorderColor(IndexedColors.BLACK.index);
        }
        return style_first_keyword;
    }

    public CellStyle getStyleDate(){
        if(style_date == null){
            style_date = wb.createCellStyle();
            CreationHelper ch = wb.getCreationHelper();
            style_date.setDataFormat(ch.createDataFormat().getFormat("dd.MM.yyyy"));
        }
        return style_date;
    }

    public CellStyle getStyleTimeRightAlign(){
        if(style_time_right_align == null){
            style_time_right_align = wb.createCellStyle();
            style_time_right_align.setAlignment(CellStyle.ALIGN_RIGHT);
        }
        return style_time_right_align;
    }

    public CellStyle getStyleHeader(){
        if(style_header == null){
            style_header = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style_header.setFont(font);
            if(wb instanceof HSSFWorkbook){
                ((HSSFWorkbook) wb).getCustomPalette().setColorAtIndex(IndexedColors.PALE_BLUE.getIndex(), (byte) 89, (byte) 164, (byte) 199);
                style_header.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            }
            else
                ((XSSFCellStyle) style_header).setFillForegroundColor(new XSSFColor(new Color(89, 164, 199)));
            style_header.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style_header.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_header.setAlignment(CellStyle.ALIGN_LEFT);
            style_header.setBorderBottom(CellStyle.BORDER_THIN);
            style_header.setBorderLeft(CellStyle.BORDER_THIN);
            style_header.setBorderTop(CellStyle.BORDER_THIN);
            style_header.setBorderRight(CellStyle.BORDER_THIN);
            style_header.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        }
        return style_header;
    }

    public CellStyle getStyleHeaderKeyword(){
        if(style_header_header == null){
            style_header_header = wb.createCellStyle();
            if(wb instanceof HSSFWorkbook){
                ((HSSFWorkbook) wb).getCustomPalette().setColorAtIndex(IndexedColors.PALE_BLUE.getIndex(), (byte) 89, (byte) 164, (byte) 199);
                style_header_header.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            }
            else
                ((XSSFCellStyle) style_header_header).setFillForegroundColor(new XSSFColor(new Color(89, 164, 199)));
            style_header_header.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style_header_header.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_header_header.setAlignment(CellStyle.ALIGN_CENTER);
            style_header_header.setBorderBottom(CellStyle.BORDER_THIN);
            style_header_header.setBorderLeft(CellStyle.BORDER_THIN);
            style_header_header.setBorderTop(CellStyle.BORDER_THIN);
            style_header_header.setBorderRight(CellStyle.BORDER_THIN);
            style_header_header.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_header.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_header.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_header.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        }
        return style_header_header;
    }

    public CellStyle getStyleHeaderFirstKeyword(){
        if(style_header_first_keyword == null){
            style_header_first_keyword = wb.createCellStyle();
            if(wb instanceof HSSFWorkbook){
                ((HSSFWorkbook) wb).getCustomPalette().setColorAtIndex(IndexedColors.PALE_BLUE.getIndex(), (byte) 89, (byte) 164, (byte) 199);
                style_header_first_keyword.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            }
            else
                ((XSSFCellStyle) style_header_first_keyword).setFillForegroundColor(new XSSFColor(new Color(89, 164, 199)));
            style_header_first_keyword.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_header_first_keyword.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style_header_first_keyword.setAlignment(CellStyle.ALIGN_CENTER);
            style_header_first_keyword.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_first_keyword.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_first_keyword.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style_header_first_keyword.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_first_keyword.setBorderBottom(CellStyle.BORDER_THIN);
            style_header_first_keyword.setBorderLeft(CellStyle.BORDER_THICK);
            style_header_first_keyword.setBorderTop(CellStyle.BORDER_THIN);
            style_header_first_keyword.setBorderRight(CellStyle.BORDER_THIN);
        }
        return style_header_first_keyword;
    }

    public CellStyle getStyleHeaderLeftBold(){
        if(style_header_left_bold == null){
            style_header_left_bold = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style_header_left_bold.setFont(font);
            if(wb instanceof HSSFWorkbook){
                ((HSSFWorkbook) wb).getCustomPalette().setColorAtIndex(IndexedColors.PALE_BLUE.getIndex(), (byte) 89, (byte) 164, (byte) 199);
                style_header_left_bold.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            }
            else
                ((XSSFCellStyle) style_header_left_bold).setFillForegroundColor(new XSSFColor(new Color(89, 164, 199)));
            style_header_left_bold.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_header_left_bold.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style_header_left_bold.setAlignment(CellStyle.ALIGN_CENTER);
            style_header_left_bold.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_left_bold.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_left_bold.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style_header_left_bold.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_header_left_bold.setBorderBottom(CellStyle.BORDER_THIN);
            style_header_left_bold.setBorderLeft(CellStyle.BORDER_THICK);
            style_header_left_bold.setBorderTop(CellStyle.BORDER_THIN);
            style_header_left_bold.setBorderRight(CellStyle.BORDER_THIN);
        }
        return style_header_left_bold;
    }

    public CellStyle getStyleTotalSummaryRowLeftBold() {
        if(style_total_summary_row_left_bold == null){
            style_total_summary_row_left_bold = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style_total_summary_row_left_bold.setFont(font);
            style_total_summary_row_left_bold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            style_total_summary_row_left_bold.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_total_summary_row_left_bold.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style_total_summary_row_left_bold.setAlignment(CellStyle.ALIGN_RIGHT);
            style_total_summary_row_left_bold.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_total_summary_row_left_bold.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_total_summary_row_left_bold.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style_total_summary_row_left_bold.setBorderBottom(CellStyle.BORDER_THIN);
            style_total_summary_row_left_bold.setBorderLeft(CellStyle.BORDER_THICK);
            style_total_summary_row_left_bold.setBorderTop(CellStyle.BORDER_THIN);
        }
        return style_total_summary_row_left_bold;
    }

    public CellStyle getStyleTotalSummaryRow() {
        if(style_total_summary_row == null){
            style_total_summary_row = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style_total_summary_row.setFont(font);
            style_total_summary_row.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            style_total_summary_row.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style_total_summary_row.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style_total_summary_row.setAlignment(CellStyle.ALIGN_RIGHT);
            style_total_summary_row.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_total_summary_row.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style_total_summary_row.setBorderBottom(CellStyle.BORDER_THIN);
            style_total_summary_row.setBorderTop(CellStyle.BORDER_THIN);
        }
        return style_total_summary_row;
    }

}