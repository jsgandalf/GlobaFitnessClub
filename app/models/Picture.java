package models;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    public String amazonKey;

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

    public String getAmazonKey(){
        return amazonKey;
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    public List<Picture_comment> getComments(){
        List<Picture_comment> comments = Picture_comment.find("select c from Picture_comment c where c.picture = ? order by postedAt asc", this).fetch();
        return comments;
    }

    public String getProfileThumbPic(){
        User thisUser = User.findById(this.album.author.id);
        return thisUser.getProfileThumbPic();
    }

    public void deleteComments(){
        List<Picture_comment> comments = Picture_comment.find("byPicture", this).fetch();
        Iterator<Picture_comment> iterator = comments.iterator();
        while (iterator.hasNext()) {
            iterator.next().delete();
        }
    }
    public void deletePicture(){
        this.deleteComments();
        String accessKey = "AKIAIIDVPNAYFEVBVVFA";
        String secretKey = "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";
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
        //Delete everything off of amazon s3
        s3.deleteObject(new DeleteObjectRequest("globafitnessphotos",this.getAmazonKey()));
        s3.deleteObject(new DeleteObjectRequest("globafitnessphotos",this.amazonThumbnailKey));
        this.delete();
    }

    public int getCommentCount(){
        List<Picture_comment> comments = Picture_comment.find("select c from Picture_comment c where c.picture = ? order by postedAt asc", this).fetch();
        return comments.size();
    }

    public String getThumb(){
        return "https://s3.amazonaws.com/globafitnessphotos/"+this.amazonThumbnailKey;
    }

}