package models;

import java.util.*;
import javax.persistence.*;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import play.Logger;
import play.data.binding.*;
import play.data.validation.*;
import play.db.jpa.Model;

@Entity
public class Album extends Model {

    @Required
    public String title;

    @Required @As("yyyy-MM-dd")
    public Date creationDate;

    @Lob
    @Required
    @MaxSize(50000)
    public String content;

    @Required
    @ManyToOne
    public User author;

    @OneToMany(mappedBy="album", cascade=CascadeType.ALL)
    public List<Picture> pictures;

    public Album(User author, String title, String content) {
        this.pictures = new ArrayList<Picture>();
        this.author = author;
        if(title.equals("")){
            List<Album> albums = Album.find("byAuthor",author).fetch();
            Integer myCount =  albums.size()+1;
            this.title = "Album " +myCount ;
        }else{
            this.title = title;
        }
        this.content = content;
        this.creationDate = new Date();
    }

    public String getFirstPictureKeyThumb(){
        //Default picture
        String amazonKey ="defaultAlbum.jpg";
        if(!this.pictures.isEmpty()){
            amazonKey = this.pictures.get(0).amazonThumbnailKey;
        }
        return amazonKey;
    }

    public String getNewKey(String fileExtension){
        String userid =  "usr" + this.author.id;
        String albumid = "alb" + this.id;
        Integer picturesLength = 0;
        if(this.pictures.isEmpty())
            picturesLength = 0;
        else
            picturesLength = this.pictures.size();
        picturesLength++;
        String picString = ""+picturesLength;
        String pictureLengthString = "pic"+(picString);
        return userid + albumid + pictureLengthString + fileExtension;
    }

    public String getNewThumbnailKey(String fileExtension){
        String userid =  "usr" + this.author.id;
        String albumid = "alb" + this.id;
        Integer picturesLength = 0;
        if(this.pictures.isEmpty())
            picturesLength = 0;
        else
            picturesLength = this.pictures.size();
        picturesLength++;
        String picString = ""+picturesLength;
        String pictureLengthString = "pic"+(picString);
        return userid + albumid + pictureLengthString + "thumb" +fileExtension;
    }

    public Album addPicture(String title, String content, String fileExtension) {
        Picture newPicture = new Picture(this, title, content, fileExtension, getNewKey(fileExtension),getNewThumbnailKey(fileExtension));
        this.pictures.add(newPicture);
        this.save();
        return this;
    }

    public void deletePictures(){
        String accessKey = "AKIAIIDVPNAYFEVBVVFA";
        String secretKey = "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";
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
        //Delete everything off of amazon s3
        Iterator<Picture> iterator = this.pictures.iterator();
        while (iterator.hasNext()) {
            Picture myPicture =  iterator.next();
            //Logger.info("Name of the file %s", myPicture.getAmazonKey());
            s3.deleteObject(new DeleteObjectRequest("globafitnessphotos",myPicture.getAmazonKey()));
            s3.deleteObject(new DeleteObjectRequest("globafitnessphotos",myPicture.amazonThumbnailKey));
        }
    }

    public Album previous() {
        return Album.find("select p from Album p where p.title = ? and creationDate < ? order by postedAt desc", author, creationDate).first();
    }

    public Album next() {
        return Album.find("select p from Album p where p.author = ? and creationDate > ? order by postedAt asc", author, creationDate).first();
    }


    public String toString() {
        return title;
    }

}
