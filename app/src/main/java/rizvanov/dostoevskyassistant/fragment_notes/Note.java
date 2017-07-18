package rizvanov.dostoevskyassistant.fragment_notes;

import java.io.Serializable;

/**
 * Created by Ильшат on 14.07.2017.
 */

public class Note implements Serializable{
    private String date;
    private String text;
    private long time;

    public Note(String date, String text, long time) {
        this.date = date;
        this.text = text;
        this.time = time;

    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
