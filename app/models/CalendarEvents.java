package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class CalendarEvents extends Model {

    @Required
    @ManyToOne
    public User author;

    public String what;

    public Date start;
    public Date end;

    public CalendarEvents(User author, String what, Date start, Date end) {
        this.author = author;
        this.what = what;
        this.start = start;
        this.end = end;
    }

    public String toString() {
        return this.author.fullName()+ "=> "+this.start;
    }
}
