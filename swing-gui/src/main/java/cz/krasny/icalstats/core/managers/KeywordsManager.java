package cz.krasny.icalstats.core.managers;

import cz.krasny.icalstats.data.classes.Keyword;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Class providing access to file with keywords.
 * @author Tomas
 */
public class KeywordsManager {
    
    /* Loads keywords from specified path. */
    public static List<Keyword> loadKeywords(String path_from){
        List<Keyword> kw_list = new ArrayList<>();
        BufferedReader input = null;
        try {
            String line = "", keyword = "", occur = "";
            String[] array;
            input = FileManager.getInstance().openReader(path_from);
            if(input == null) return kw_list;
            while((line = input.readLine()) != null){
                array = line.split(";");
                keyword = array[0];
                occur = array[1];
                kw_list.add(new Keyword(keyword, Integer.valueOf(occur)));
            }
            input.close();
        }
        catch (IOException | NumberFormatException ex) {
            
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return kw_list;
        }
        return kw_list;
    }
    
    /* Saves keywords to specified path. */
    public static void saveKeywords(List<Keyword> kw_list, String path_to){
        try{
            String content;
            StringBuilder builder = new StringBuilder();
            for(Keyword kw: kw_list){
                builder.append(kw.getKeyword());
                builder.append(";");
                builder.append(kw.getOccurrence());
                builder.append(System.lineSeparator());
            }
            content = builder.toString();
            FileManager.getInstance().saveFile(path_to, content);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
