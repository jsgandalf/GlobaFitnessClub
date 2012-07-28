package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import flexjson.JSONSerializer;
import models.*;
import play.Logger;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FitnessBlogs extends Controller{

    @Before
    static void addUser() {
        User user = connected();
        if(user != null) {
            renderArgs.put("user", user);
            session.put("isHome","true");
		}
    }

    static User connected() {
        if(renderArgs.get("user") != null) {
            return renderArgs.get("user", User.class);
        }
        String email = session.get("user");
        if(email != null) {
            return User.find("byEmail", email).first();
        }
        return null;
    }

	public static void index(){
		User user = connected();
        List<FitnessBlog> blogs = FitnessBlog.find("author = ? order by month ASC", user).fetch();
        render(user,blogs);
	}

    public static void myWorkoutSheets(){
        User user = connected();
        render(user);
    }
    public static void factOrFiction(){
        User user = connected();
        List<Blog> blogs = Blog.find("author = ? and type = ?",user,1).fetch();
        render(user,blogs);
    }
    public static void motivationalQuotes(){
        User user = connected();
        Selected selected = Selected.find("author = ?",user).first();
        Quotes quoteSelected;
        if(selected == null){
            quoteSelected = Quotes.find("author = ? or default_quote = ? order by id asc", user, true).first();
            Logger.info(""+quoteSelected.id);
        }else{
            quoteSelected = Quotes.findById(selected.quote.id);
            Logger.info("A log message"+quoteSelected.id);
        }
        List<Quotes> quotes = Quotes.find("author = ? or default_quote = ?", user, true).fetch();
        List<Blog> blogs = Blog.find("author = ? and type = ? and quote = ?",user,2,quoteSelected).fetch();
        Logger.info("A log new"+quoteSelected.id);
        render(user,blogs, quotes, quoteSelected);
    }
    public static void generalKnowledge(){
        User user = connected();
        List<Blog> blogs = Blog.find("author = ? and type = ?",user,3).fetch();
        render(user,blogs);
    }
    public static void trainingSessionInformation(){
        User user = connected();
        List<Blog> blogs = Blog.find("author = ? and type = ?",user,4).fetch();
        render(user,blogs);
    }
    public static void myFitnessEvaluation(){
        User user = connected();
        render(user);
    }
    public static void myTrainersProfile(){
        User user = connected();
        render(user);
    }
    public static void myTransformation(){
        User user = connected();
        render(user);
    }

    public static void blog(String content, Integer type){
        User user = connected();
        String success = "\"success\"";
        if(content.equals("")||content==null){
            success = "\"Please share your current status!\"";
            renderJSON("{\"success\": " + success +"}");
        }
        Blog newBlog = new Blog(user,content,type);
        newBlog.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(newBlog));
    }
    public static void BlogQuote(String content, Integer type, Long quoteid){
        User user = connected();
        String success = "\"success\"";
        if(content.equals("")||content==null){
            success = "\"Please share your current status!\"";
            renderJSON("{\"success\": " + success +"}");
        }
        Quotes quote = Quotes.findById(quoteid);
        Blog newBlog = new Blog(user,content,type, quote);
        newBlog.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(newBlog));
    }
    public static void blog_comments(Integer type, Long quoteid){
        User user = connected();
        Quotes quote = Quotes.findById(quoteid);
        List<Blog> blogs = Blog.find("author = ? and type = ? and quote = ?",user,type,quote).fetch();
        render(blogs,user, type,quote);
    }
    public static void blog_comments_quotes(Integer type, Long quoteid){
        User user = connected();
        Quotes quote = Quotes.findById(quoteid);
        List<Blog> blogs = Blog.find("author = ? and type = ? and quote = ?",user,type,quote).fetch();
        Selected select = Selected.find("author = ?",user).first();
        if(select==null)
            select = new Selected(user,quote,true);
        else
            select.quote = quote;
        select.save();
        render(blogs,user, type,quote);
    }

    public static void deleteBlog(Long id){
        Blog newBlog = Blog.findById(id);
        newBlog.deleteBlog();
    }
    public static void addComment(Long blogID, String content){
        User user = connected();
        Blog currentBlog = Blog.findById(blogID);
        Blog_comment comment = new Blog_comment(currentBlog, user, content);
        comment.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(comment));
    }
    public static void deleteComment(Long commentID){
        Blog_comment newComment = Blog_comment.findById(commentID);
        newComment.delete();
    }
    public static void uploadBlogPhoto(File photo, int month) throws IOException {
        long MAX_SIZE = 5242880;
        User user = connected();
        String accessKey = "AKIAIIDVPNAYFEVBVVFA";
        String secretKey = "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";
        if(photo==null){
            flash.error("Please upload a file");
            Settings.index();
        }
        String mimeType = play.libs.MimeTypes.getMimeType(photo.getAbsolutePath());
        if(!mimeType.equals("image/jpeg") && !mimeType.equals("image/gif") && !mimeType.equals("image/png")){
            flash.error("File is not jpg, gif, or png. Please upload this type of file.");
            Settings.index();
        }
        if(photo.length()>MAX_SIZE){
            flash.error("File is too large, must be less than 2.5mb");
            Settings.index();
        }
        String fileExtension = "";
        if(mimeType.equals("image/jpeg")) fileExtension=".jpg";
        else if(mimeType.equals("image/gif")) fileExtension=".gif";
        else if(mimeType.equals("image/png")) fileExtension=".png";
        else{
            flash.error("File is not jpg, gif, or png. Please upload this type of file.");
            Settings.index();
        }

        BufferedImage image = ImageIO.read(photo);
        int height = image.getHeight();
        int width = image.getWidth();

        int maxwidth = 360;
        int maxheight = 200;

        File newFile = new File("Foo"+fileExtension); // create random unique filename here

        if((width>=maxwidth) || height>=maxheight){
            Images.resize(photo, newFile, maxwidth, maxheight, true);
        }else
            newFile = photo;

        flash.clear();
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
        String key = user.id+"_"+month+"";
        FitnessBlog blog = new FitnessBlog(month,user,key);
        s3.putObject(new PutObjectRequest("globafitnessphotos", "blog/"+key, newFile).withCannedAcl(CannedAccessControlList.PublicRead));
        File newThumbnailFile = new File("Foo2"+fileExtension); // create random unique filename here

        if((width>=50) || height>=50){
            Images.resize(photo, newThumbnailFile, 50, 50, true);
        }else
            newThumbnailFile = newFile;
        FitnessBlog thumBlog = new FitnessBlog(month,user,"thumb"+key);
        s3.putObject(new PutObjectRequest("globafitnessphotos", "blog/thumb"+key, newThumbnailFile).withCannedAcl(CannedAccessControlList.PublicRead));
        flash.success("You have successfully updated your profile photo");
    }

}