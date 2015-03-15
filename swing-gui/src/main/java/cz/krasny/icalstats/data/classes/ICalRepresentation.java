package cz.krasny.icalstats.data.classes;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class representing ical file representation: its source and name.
 * @author Tomas Krasny
 */
public class ICalRepresentation {
        
    private String name = "";
    private String local_path = "";
    private URL url = null;
    private boolean selected = false;
    
    public ICalRepresentation(){ }
    
    public ICalRepresentation(String name, String local_path){
        this.name = name;
        this.local_path = local_path;
    }
    
    public ICalRepresentation(String name, URL url){
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return local_path;
    }

    public URL getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.local_path = path;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /* Tests if source is correct. */
    public boolean test() throws IOException{
        if(this.url == null){
            File f = new File(this.local_path);
            return f.exists();
        }
        else{
            HttpURLConnection c = (HttpURLConnection) this.url.openConnection();
            c.connect();
            return c.getResponseCode() == HttpURLConnection.HTTP_OK;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    @Override
    public String toString() {
        if(this.url == null)
            return this.name + ": " + this.local_path;
        else
            return this.name + ": " + this.url.toString();
    }
}
