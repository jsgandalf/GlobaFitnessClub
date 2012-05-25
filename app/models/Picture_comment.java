package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Picture_comment extends Model {

    @Required
    @ManyToOne
    public User author;

    public Date postedAt;

    @Lob
    @MaxSize(10000)
    public String content;

    @Required
    @ManyToOne
    public Picture picture;

    public Picture_comment(Picture picture, User author, String content) {
        this.picture = picture;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

}