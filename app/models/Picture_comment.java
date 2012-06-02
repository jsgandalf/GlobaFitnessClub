package models;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Entity
public class Picture_comment extends Model {

    @Required
    @ManyToOne
    public User author;

    public Date postedAt;

    @Required
    public String content;
    public boolean done;

    @Required
    @ManyToOne(cascade= CascadeType.PERSIST)
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
