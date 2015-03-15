package cz.krasny.icalstats.data.classes.output.formats;

import cz.krasny.icalstats.data.classes.output.ExportConfiguration;
import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.Keyword;
import cz.krasny.icalstats.data.classes.output.AOutputFormat;
import cz.krasny.icalstats.data.classes.output.OutputFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

/***
 * Class generating statistics in HTML format.
 * @author Tomas Krasny
 */
public class HTMLOutputFormat extends AOutputFormat{
    
    /* This method generates XLS statistics and then convert them into HTML format. */
    private File generateFile(List<ICalFile> ical_files, List<Keyword> keywords, ExportConfiguration ec) throws Exception{
        File file_html = null;
        File file_xlsx = null;
        String filename = getDefaultFileName() + ".html";
        ec.setOutputFormat(OutputFormat.XLSX);
        file_html = new File(filename);
        ExcelOutputFormat xls_statistics = new ExcelOutputFormat();
        file_xlsx = xls_statistics.createStatistic(ical_files, keywords, ec);
        ToHtml t = ToHtml.create(new FileInputStream(file_xlsx), new BufferedWriter(new FileWriter(file_html)));
        t.setCompleteHTML(true);
        
        t.printPage();
        return file_html;
    }
    
    /* Generates statistics. */
    @Override
    public File createStatistic(List<ICalFile> ical_files, List<Keyword> keywords, ExportConfiguration ec) throws Exception {
        File file = null;
        file = generateFile(ical_files, keywords, ec);
        return file;
    }

}

