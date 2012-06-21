package models;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Entity
public class Album extends Model {

    @Required
    public String title;

    //@Required @As("yyyy-MM-dd")
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
        String amazonKey ="default/defaultAlbum.jpg";
        if(!this.pictures.isEmpty()){
            amazonKey = this.pictures.get(0).amazonThumbnailKey;
        }
        return "https://s3.amazonaws.com/globafitnessphotos/"+amazonKey;
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

    public Picture addPicture(String title, String content, String fileExtension) {
        Picture newPicture = new Picture(this, title, content, fileExtension, getNewKey(fileExtension),getNewThumbnailKey(fileExtension));
        this.pictures.add(newPicture);
        this.save();
        return newPicture;
    }

    public Picture addPicture(String title, String content, String fileExtension, int width, int height) {
        Picture newPicture = new Picture(this, title, content, fileExtension, getNewKey(fileExtension),getNewThumbnailKey(fileExtension), width, height);
        this.pictures.add(newPicture);
        this.save();
        return newPicture;
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

            //Delete All comments with all the picture
            myPicture.deleteComments();
        }
    }

    public Integer getCommentCount(){
        Integer count = 0;
        Iterator<Picture> iterator = this.pictures.iterator();
        while (iterator.hasNext()) {
            Picture myPicture =  iterator.next();
            count +=myPicture.getComments().size();
        }
        return count;
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

    public Integer getPictureCount(){
        List <Picture> pictures = Picture.find("byAlbum",this).fetch();
        return pictures.size();
    }

}
