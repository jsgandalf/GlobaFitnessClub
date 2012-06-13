package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Share_comment extends Model {

    @Required
    @ManyToOne
    public User author;

    public Date creationDate;

    @Required
    public String content;

    @Required
    @ManyToOne(cascade= CascadeType.PERSIST)
    public Share share;

    public Share_comment(Share share, User author, String content) {
        this.share = share;
        this.author = author;
        this.content = content;
        this.creationDate = new Date();
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}
