package models;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sean
 * Date: 5/11/12
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Album_old extends Model {

    @Required
    public String title;

    @Required
    @As("yyyy-MM-dd")
    public Date creationDate;

    @Lob
    @MaxSize(1000)
    public String description;

    @Required
    @OneToMany(mappedBy="album", cascade=CascadeType.ALL)
    public List<Picture_old> myPictures;

    //Super class User
    /*@Required
    @ManyToOne
    private User user;*/

    public Album_old(String title, String description /*, User user*/) {
        this.myPictures = new ArrayList<Picture_old>();
        this.title = title;
        this.description = description;
        this.creationDate = new Date();
        //this.user = user;
    }

    public String getNewKey(){
        /*String userid =  "usr" + this.user.id;
        String albumid = "alb" + this.id;
        int picturesLength;
        if(this.myPictures.isEmpty())
            picturesLength = 0;
        else
            picturesLength = this.myPictures.size();
        picturesLength++;
        String picString = ""+picturesLength;
        String pictureLengthString = "pic"+(picString);
        return userid + albumid + pictureLengthString;*/
        return "helloWorld";
    }

    public Album_old addPicture(String title, String description){
        Picture_old picture = new Picture_old(title,description,getNewKey(),this);
        this.myPictures.add(picture);
        this.save();
        return this;
    }

    public Album_old addPicture(){
        Picture_old picture = new Picture_old("","",getNewKey(),this);
        picture.create();
        this.myPictures.add(picture);
        this.save();
        return this;
    }

    public int pictureListSize(){
        return this.myPictures.size();
    }

    public void removePicture(){
        //Picture_old picture = findBy
        //this.pictures.remove(picture);
    }
}
