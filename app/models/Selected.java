package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Selected extends Model {

    @ManyToOne
    @Required
    public User author;

    @ManyToOne
    @Required
    public Quotes quote;
    public Boolean isSelected;

    public Selected(User author, Quotes quote, Boolean isSelected) {
        this.author = author;
        this.isSelected = isSelected;
        this.quote = quote;
        this.quote = quote;
    }

}