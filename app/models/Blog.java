package models;

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
public class Blog extends Model {

    @Required @As("yyyy-MM-dd")
    public Date creationDate;

    @Lob
    @Required
    @MaxSize(10000)
    public String content;

    @ManyToOne
    @Required
    public User author;

    //1 for fact or fiction
    //2 for Motivational Quotes
    //3 General Knowledge
    //4 Training/Sesssion Information
    public Integer type;

    public String link;
    //Thumbnail of the image
    public String photo;
    public String videoName;

    public Blog(User author, String content, Integer type) {
        this.author = author;
        this.content = content;
        this.creationDate = new Date();
        this.type=type;
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    public List<Blog_comment> getComments(){
        List<Blog_comment> comments = Blog_comment.find("byBlog", this).fetch();
        return comments;
    }



    public void deleteComments(){
        List<Blog_comment> comments = Blog_comment.find("byBlog", this).fetch();
        Iterator<Blog_comment> iterator = comments.iterator();
        while (iterator.hasNext()) {
            iterator.next().delete();
        }
    }
    public void deleteBlog(){
        this.deleteComments();
        this.delete();
    }

    public int getCommentCount(){
        List<Blog_comment> comments = Blog_comment.find("byBlog", this).fetch();
        return comments.size();
    }

    public String getPhotoFromThumb(){
        Picture currentPicture = Picture.find("byAmazonThumbnailKey",this.photo).first();
        if(currentPicture==null)
            return "";
        return currentPicture.getAmazonKey();
    }

}