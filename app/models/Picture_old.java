package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sean
 * Date: 5/10/12
 * Time: 9:18 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Picture_old extends Model {
    public String title;

    @Required @As("yyyy-MM-dd")
    public Date creationDate;

    @Lob
    @MaxSize(1000)
    public String description;

    //The key found in AWS Amazon s3
    public String key;

    @ManyToOne
    @Required
    public Album album;

    public Picture_old(String title, String description, String key, Album album) {
        this.title = title;
        this.description = description;
        this.key = key;
        this.creationDate = new Date();
        this.album = album;
    }
}