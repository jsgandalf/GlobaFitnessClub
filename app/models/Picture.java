package models;

import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import play.data.binding.As;
import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class Picture extends Model {

    @Required
    public String title;

    @Required @As("yyyy-MM-dd")
    public Date creationDate;

    @Lob
    @Required
    @MaxSize(10000)
    public String content;

    @ManyToOne
    @Required
    public Album album;

    @Required
    private String amazonKey;

    public String fileExtension;

    public String amazonThumbnailKey;



    public Picture(Album album, String title, String content, String fileExtension, String amazonKey, String amazonThumbnailKey) {
        this.album = album;
        this.title = title;
        this.content = content;
        this.creationDate = new Date();
        this.fileExtension = fileExtension;
        this.amazonKey = amazonKey;
        this.amazonThumbnailKey = amazonThumbnailKey;
        //this.picture_coments = new ArrayList<Picture_comment>();
    }

    /*public Picture_comment addComent(String comment_content){
        Picture_comment newComment = new Picture_comment(this, album.author, comment_content);
        this.picture_coments.add(newComment);
        this.save();
        return newComment;
    }*/

    public String getAmazonKey(){
        return amazonKey;
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    public List<Picture_comment> getComments(){
        List<Picture_comment> comments = Picture_comment.find("select c from Picture_comment c where c.picture = ? order by postedAt desc", this).fetch();
        return comments;
    }

}