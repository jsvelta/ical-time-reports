package cz.krasny.icalstats.data.classes.output;

import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.Keyword;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Abstract class as a parent for other output format.
 * @author Tomas Krasny
 */
public abstract class AOutputFormat {
   
    protected String getDefaultFileName(){
        String path = System.getProperty("java.io.tmpdir") + File.separator;
        path += "Statistics_";
        path += new SimpleDateFormat("dd.MM.yyyy").format(new Date())/*+UUID.randomUUID()*/;
        return path;
    }
    
    /* Abstract method generating statistics. */
    public abstract File createStatistic(List<ICalFile> ical_files, List<Keyword> keywords, ExportConfiguration ec) throws Exception;

}


