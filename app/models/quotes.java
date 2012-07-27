package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Quotes extends Model {

    @Required
    @ManyToOne
    public User author;

    public Date creationDate;

    public String path;

    public Boolean default_quote;

    public Quotes(User author, String path) {
        this.author = author;
        this.path = path;
        this.creationDate = new Date();
    }
}
