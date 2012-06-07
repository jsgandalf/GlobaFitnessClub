package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class CalendarEvent extends Model {

    @Required
    @ManyToOne
    public User author;

    public String timeFrom;
    public String timeTo;
    public String what;
    public Date start;

    public CalendarEvent(User author, String timeFrom, String timeTo, String what, Date start) {
        this.author = author;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.what = what;
        this.start = start;
    }

    public String toString() {
        return this.author.fullName()+ "=> "+this.start;
    }
}
