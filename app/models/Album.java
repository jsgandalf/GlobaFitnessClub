package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: Sean
 * Date: 5/11/12
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class Album extends Model {

    @Required
    public String title;

    @Required
    @As("yyyy-MM-dd")
    public Date creationDate;

    @Lob
    @MaxSize(1000)
    public String description;

    @OneToMany(mappedBy="album", cascade= CascadeType.ALL)
    public List<Picture> pictures;

    @Required
    @ManyToOne
    public User author;

    public Album(User author, String title, String description) {
        this.pictures = new ArrayList<Picture>();
        this.author = author;
        this.title = title;
        this.description = description;
        this.creationDate = new Date();
    }

    public Album addPicture(String title, String description){
        String key = this.author.id + this.id + (this.pictures.size()+1)+"";
        Picture picture = new Picture(title,description,key);
        this.pictures.add(picture);
        this.save();
        return this;
    }

    public void removePicture(){
        //Picture picture = findBy
        //this.pictures.remove(picture);
    }
}
