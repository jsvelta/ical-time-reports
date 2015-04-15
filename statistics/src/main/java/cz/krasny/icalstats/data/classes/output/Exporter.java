package cz.krasny.icalstats.data.classes.output;

import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.Keyword;
import cz.krasny.icalstats.data.classes.output.formats.ExcelOutputFormat;
import cz.krasny.icalstats.data.classes.output.formats.HTMLOutputFormat;
import java.io.File;
import java.util.List;
import java.util.zip.DataFormatException;

/***
 * Class representin interface for generating statistics.
 * @author Tomas Krasny
 */
public class Exporter {

    /* Method for generating statistics. */
    public File export(List<ICalFile> ical_files, List<Keyword> keywords, ExportConfiguration export_configuration) throws Exception{
        AOutputFormat output = null;
        checkExportConfiguration(ical_files, keywords, export_configuration);
        switch(export_configuration.getOutputFormat()){
            case HTML: output = new HTMLOutputFormat(); break;
            case XLS: output = new ExcelOutputFormat(); break;
            case XLSX: output = new ExcelOutputFormat(); break;
            default: throw new IllegalArgumentException("Unknow output format.");
        }

        File file = output.createStatistic(ical_files, keywords, export_configuration);

        return file;
    }

    /* Checks if the export configuration is set correctly. */
    private void checkExportConfiguration(List<ICalFile> ical_files, List<Keyword> keywords, ExportConfiguration ec) throws DataFormatException{
        if(ec.getOutputFormat() == null)
            throw new NullPointerException("OutputFormat can not be null.");
        if(ical_files.isEmpty())
            throw new IllegalArgumentException("At least one iCal file must be selected.");
        if(keywords == null)
            throw new NullPointerException("Keywords can not be null.");
        if(keywords.isEmpty())
            throw new IllegalArgumentException("Keywords count is 0.");
        if(ec.getGroupBy() == null)
            throw new NullPointerException("Granularity can not be null.");
        if(ec.getUnit() == null)
            throw new NullPointerException("Unit can not be null.");
        if(!ec.getDateRange().isIncludedWholeCalendar()){
            if(ec.getDateRange().getDateTo() == null && ec.getDateRange().getDateFrom() == null)
                throw new IllegalArgumentException("Wrong date range.");
            else if(ec.getDateRange().getDateTo() != null && ec.getDateRange().getDateFrom() != null)
                if(ec.getDateRange().getDateFrom().getTime().after(ec.getDateRange().getDateTo().getTime()))
                    throw new IllegalArgumentException("Wrong date order.");
        }
    }
}




