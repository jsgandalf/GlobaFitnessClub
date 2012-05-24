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
    }

    public String getAmazonKey(){
        return amazonKey;
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

}