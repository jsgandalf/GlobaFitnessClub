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
public class Share extends Model {

    @Required @As("yyyy-MM-dd")
    public Date creationDate;

    @Lob
    @Required
    @MaxSize(10000)
    public String content;

    @ManyToOne
    @Required
    public User author;

    public Share(User author, String content) {
        this.author = author;
        this.content = content;
        this.creationDate = new Date();
    }

    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    public List<Share_comment> getComments(){
        List<Share_comment> comments = Share_comment.find("byShare", this).fetch();
        return comments;
    }



    public void deleteComments(){
        List<Share_comment> comments = Share_comment.find("byShare", this).fetch();
        Iterator<Share_comment> iterator = comments.iterator();
        while (iterator.hasNext()) {
            iterator.next().delete();
        }
    }
    public void deleteShare(){
        this.deleteComments();
        this.delete();
    }

    public int getCommentCount(){
        List<Share_comment> comments = Share_comment.find("byShare", this).fetch();
        return comments.size();
    }

}