package cz.krasny.icalstats.core.managers;

import cz.krasny.icalstats.data.classes.ICalFile;
import cz.krasny.icalstats.data.classes.ICalRepresentation;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import net.fortuna.ical4j.data.CalendarBuilder;

/***
 * Class providing loading ical files. Design pattern: singleton.
 * @author Tomas Krasny
 */
public class ICSFileManager {
    
    /* Instance */
    private static ICSFileManager instance = null;
    
    /* File manager instance */
    private static FileManager file_manager = FileManager.getInstance();
    
    /* Private constructor */
    private ICSFileManager(){}
    
    /* Returns singleton's instance */
    public static ICSFileManager getInstance(){
        if(instance == null) instance = new ICSFileManager();
        return instance;
    }
    
    /* Creates reader to specified ical file. */
    private Reader getReader(ICalRepresentation icr) throws FileNotFoundException, IOException{
        BufferedReader reader = null;
        if(icr.getUrl() == null)
            reader = file_manager.openReader(icr.getPath());
        else
            reader = file_manager.openReader(icr.getUrl());
        return reader;
    }
    
    /* Loads calendar file from its source. */
    public ICalFile loadICalFile(ICalRepresentation icr) throws Exception{
        ICalFile ical_file = null;
        CalendarBuilder builder = new CalendarBuilder();
        Reader reader;
        reader = getReader(icr);
        net.fortuna.ical4j.model.Calendar calendar = builder.build(reader);
        ical_file = new ICalFile(calendar);
        reader.close();
        return ical_file;
    }
}