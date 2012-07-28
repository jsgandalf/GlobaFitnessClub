package models;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Quotes extends Model {

    @Required @As("yyyy-MM-dd")
    public Date creationDate;

    @ManyToOne
    @Required
    public User author;

    public String path;

    public Boolean default_quote;

    public Boolean selected;

    public Quotes(User author, String path) {
        this.author = author;
        this.creationDate = new Date();
        this.path = path;
        this.default_quote = false;
        this.selected = true;
    }

}