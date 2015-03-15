package cz.krasny.icalstats.core.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import org.apache.commons.io.FileUtils;

/***
 * Class providing access to files. Design pattern: singleton.
 * @author Tomas Krasny
 */
public class FileManager {
    
    /** Instance */
    private static FileManager instance = null;
    
    /* Returns instance. */
    public static FileManager getInstance(){
        if(instance == null) instance = new FileManager();
        return instance;
    }
    
    /* Private constructor. */
    private FileManager(){}
    
    /* Opens buffered reader to specified path. */
    public BufferedReader openReader(String path) throws FileNotFoundException, UnsupportedEncodingException{
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
        return br;
    }
    
    /* Opens buffered reader to specified URL. */
    public BufferedReader openReader(URL url) throws UnsupportedEncodingException, IOException {
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
        return br;
    }    
    
    /* Saves string content to specified path. */
    public void saveFile(String path_with_filename, String content) throws UnsupportedEncodingException, FileNotFoundException, IOException{
        BufferedWriter bw;
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path_with_filename),"UTF-8"));
        bw.write(content);
        bw.close();
    }
    
    /* Saves specified file to specified path. */
    public File saveFileToFolder(String path_folder, File file) throws IOException{
        File file_destination;
        file_destination = new File(path_folder);
        FileUtils.copyFile(file, file_destination);
        return file_destination;
    }
}
