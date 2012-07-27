package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Blog_comment extends Model {

    @Required
    @ManyToOne
    public User author;

    public Date creationDate;

    @Required
    public String content;

    @Required
    @ManyToOne(cascade= CascadeType.PERSIST)
    public Blog blog;

    public Blog_comment(Blog blog, User author, String content) {
        this.blog = blog;
        this.author = author;
        this.content = content;
        this.creationDate = new Date();
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}
