package cz.krasny.icalstats.data.classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Class used to logging exceptions.
 * @author Tomas
 */
public class Logger {
    
    private static final String log_name = "log.txt";
    
    /* Appends text to log file. */
    public static void appendToLog(String text){
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(log_name, true)))) {
            out.append(text);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* Appends exception to log. */
    public static void appendException(Exception ex) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(log_name, true)))) {
            out.append(System.getProperty("line.separator"));
            out.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())+"\t");
            out.append(ex.getMessage());
            out.append(System.getProperty("line.separator"));
            for(StackTraceElement s: ex.getStackTrace()){
                out.append(s.toString());
                out.append(System.getProperty("line.separator"));
            }
        } catch (IOException ex1) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    
}
