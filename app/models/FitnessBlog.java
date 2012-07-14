package models;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created with IntelliJ IDEA.
 * User: Sean
 * Date: 7/7/12
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class FitnessBlog extends Model {

    public int month;
    public String amazonkey;

    @Required
    @ManyToOne
    public User author;

    public FitnessBlog(int month, User author, String amazonkey){
        this.month = month;
        this.author = author;
        this.amazonkey = amazonkey;
        this.save();
    }

    public void deleteBlog(){
        String accessKey = "AKIAIIDVPNAYFEVBVVFA";
        String secretKey = "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";
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
        //Delete everything off of amazon s3
        s3.deleteObject(new DeleteObjectRequest("globafitnessphotos",this.amazonkey));
        s3.deleteObject(new DeleteObjectRequest("globafitnessphotos","thumb"+this.amazonkey));
        this.delete();
    }

}
