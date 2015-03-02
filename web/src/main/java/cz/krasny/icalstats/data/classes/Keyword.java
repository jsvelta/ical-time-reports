package cz.krasny.icalstats.data.classes;

/***
 * Class representing keywords with its occurrence.
 * @author Tomas Krasny
 */
public class Keyword {
    
    private String keyword = "";
    private int occurrence = 0;
    
    public Keyword(String keyword, int occurrence){
        this.keyword = keyword;
        this.occurrence = occurrence;
    }
    
    public String getKeyword() {
        return keyword;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    @Override
    public String toString() {
        return this.occurrence + " " + this.keyword;
    }
}
