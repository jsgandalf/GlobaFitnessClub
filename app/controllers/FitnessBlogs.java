package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import models.FitnessBlog;
import models.User;
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