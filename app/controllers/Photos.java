 package controllers;

 import com.amazonaws.auth.AWSCredentials;
 import com.amazonaws.auth.BasicAWSCredentials;
 import com.amazonaws.services.s3.AmazonS3;
 import com.amazonaws.services.s3.AmazonS3Client;
 import com.amazonaws.services.s3.model.*;

 import com.amazonaws.services.s3.transfer.TransferManager;
 import com.amazonaws.services.s3.transfer.Upload;
 import com.google.gson.JsonSerializer;
 import com.ning.http.client.FilePart;
 import flexjson.JSONSerializer;
 import models.*;
 import org.apache.commons.io.IOUtils;
 import play.Logger;
 import play.Play;
 import play.data.FileUpload;
 import play.db.Model;
 import play.mvc.Before;
 import play.mvc.Controller;

 import javax.imageio.ImageIO;
 import java.awt.image.BufferedImage;
 import java.io.*;
 import java.util.Iterator;
 import java.util.List;

 import static play.libs.Images.resize;

 public class Photos extends Controller {
     @Before
     static void addUser() {

         User user = connected();
         if(user != null) {
             renderArgs.put("user", user);
         }
     }

     @Before
     static void checkUser(){
         if(connected() == null) {
             flash.error("Please log in first to view photos.");
             Application.login_page();
         }
     }
     private static long MAX_SIZE = 2621440;

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

     public static void index() {
         User user = connected();
         List<Album> myAlbumList = Album.find(
                 "select a from Album a where a.author = ? order by creationDate desc",user).fetch(10);
         render(user, myAlbumList);
     }

     public static void showAlbum(Long albumID) {
         if(connected() != null) {
             User user = connected();
             Album currentAlbum = Album.findById(albumID);
             render(user, currentAlbum);
         }
         flash.error("Please log in first to view photos.");
         Application.login_page();
     }

     public static void addAlbum(){
         User user = connected();
         render(user);
     }

     public  static void addAlbumPhotos(Long albumID){
         User user = connected();
         Album currentAlbum = Album.findById(albumID);
         render(user,currentAlbum);
     }

     public  static void addAlbumPhotos(Long albumID, String photo_title, String message){
         User user = connected();
         Album currentAlbum = Album.findById(albumID);
         render(user,currentAlbum,photo_title,message);
     }

     public static void saveAlbumDetails(String album_title, String message, Long albumID) {
         User user = connected();
         Album currentAlbum = new Album(user,album_title,message);
         currentAlbum.create();
         addAlbumPhotos(currentAlbum.id);
     }


     public static void deletePhoto(Long id){
        Picture myPicture = Picture.findById(id);
        myPicture.deletePicture();
     }


     public static void copyAmazonPicture(String amazonKey, String keyToFetch, Integer maxwidth, Integer maxheight, Picture newPicture) throws IOException {
         String accessKey = "AKIAIIDVPNAYFEVBVVFA";
         String secretKey = "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";
         AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
         S3Object object = s3.getObject(
                 new GetObjectRequest("globafitnessphotos", keyToFetch));
         InputStream objectData = object.getObjectContent();
        // Process the objectData stream.
         File f=new File("outFile.java");
         OutputStream out=new FileOutputStream(f);
         byte buf[]=new byte[1024];
         int len;
         while((len=objectData.read(buf))>0)
             out.write(buf,0,len);
         out.close();

         objectData.close();

         BufferedImage image = ImageIO.read(f);
         int height = image.getHeight();
         int width = image.getWidth();
         File resizedPhoto = new File(f.getName());
         resizedPhoto.createNewFile();
         if((width>=maxwidth) || height>=maxheight){
             resize(f,resizedPhoto,maxwidth,maxheight,true);
             f = resizedPhoto;
         }
         s3.putObject(new PutObjectRequest("globafitnessphotos", amazonKey, f).withCannedAcl(CannedAccessControlList.PublicRead));
         String flashError = "success";
         renderJSON("{\"success\": " + flashError +"}");
     }

     public static void uploadPhotoToAlbum(String photo_title, String message, File photo2, Long albumID) throws IOException {
         User user = connected();
         String accessKey = "AKIAIIDVPNAYFEVBVVFA";
         String secretKey = "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";
         String stringText = "psd-delete-icon.jpg";
         String names = "hello ";
         //String mimeType = play.libs.MimeTypes.getMimeType(photo2.getAbsolutePath());
         //    Logger.info("\nSize = %d", names);
         /*File targetFile = new File("tmp/" + stringText);
         IOUtils.copy(request.body, new FileOutputStream(targetFile));
         Logger.info("\nSize = %d", targetFile.length());*/
         //renderJSON("{\"success\": " + names+"}");

         /*if(photo2==null){
             String flashError="Please upload a file";
             renderJSON("{\"success\": " + flashError +"}");
         }
         String mimeType = play.libs.MimeTypes.getMimeType(photo2.getAbsolutePath());
         if(!mimeType.equals("image/jpeg") && !mimeType.equals("image/gif") && !mimeType.equals("image/png")){
             String flashError = "File is not jpg, gif, or png. Please upload this type of file.";
             renderJSON("{\"success\": " + flashError +"}");
         }
         if(photo2.length()>MAX_SIZE){
             String flashError = "File is too large, must be less than 2.5mb";
             renderJSON("{\"success\": " + flashError +"}");
         }*/
         String fileExtension = ".jpg";
         /*if(mimeType.equals("image/jpeg")) fileExtension=".jpg";
         else if(mimeType.equals("image/gif")) fileExtension=".gif";
         else if(mimeType.equals("image/png")) fileExtension=".png";
         else{
             String flashError = "File is not jpg, gif, or png. Please upload this type of file.";
             renderJSON("{\"success\": " + flashError +"}");
         }*/

         BufferedImage image = ImageIO.read(photo2);
         int height = image.getHeight();
         int width = image.getWidth();
         File resizedPhoto = new File(photo2.getName());
         resizedPhoto.createNewFile();
         if((width>=800) || height>=640){
             resize(photo2,resizedPhoto,800,640,true);
             photo2 = resizedPhoto;
         }

         flash.clear();
         Album currentAlbum = Album.findById(albumID);
         String amazonkey = currentAlbum.getNewKey(fileExtension);
         String amazonThumbnailKey = currentAlbum.getNewThumbnailKey(fileExtension);
         Picture newPicture = currentAlbum.addPicture(photo_title, message, fileExtension);
         AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
         s3.putObject(new PutObjectRequest("globafitnessphotos", amazonkey, photo2).withCannedAcl(CannedAccessControlList.PublicRead));
         copyAmazonPicture(amazonThumbnailKey, amazonkey, 180, 120, newPicture);
         String flashError = "success";
         renderJSON("{\"success\": " + flashError +"}");
     }

     public static void uploadProfilePhoto(File photo) throws IOException {
         User user = connected();
         String accessKey = "AKIAIIDVPNAYFEVBVVFA";
         String secretKey = "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";
         if(photo==null){
             flash.error("Please upload a file");
             render("@Settings.index", user);
         }
         String mimeType = play.libs.MimeTypes.getMimeType(photo.getAbsolutePath());
         if(!mimeType.equals("image/jpeg") && !mimeType.equals("image/gif") && !mimeType.equals("image/png")){
             flash.error("File is not jpg, gif, or png. Please upload this type of file.");
             render("@Settings.index", user, photo);
         }
         if(photo.length()>MAX_SIZE){
             flash.error("File is too large, must be less than 2.5mb");
             render("@Settings.index", user, photo);
         }
         String fileExtension = "";
         if(mimeType.equals("image/jpeg")) fileExtension=".jpg";
         else if(mimeType.equals("image/gif")) fileExtension=".gif";
         else if(mimeType.equals("image/png")) fileExtension=".png";
         else{
             flash.error("File is not jpg, gif, or png. Please upload this type of file.");
             render("@Settings.index", user);
         }
         flash.clear();
         AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
         File resizedPhoto = new File(photo.getName());
         resizedPhoto.createNewFile();
         resize(photo,resizedPhoto,200,200,true);
         photo = resizedPhoto;
         s3.putObject(new PutObjectRequest("globafitnessphotos", "profile"+user.id+fileExtension, photo).withCannedAcl(CannedAccessControlList.PublicRead));
         user.profilePicture("profile"+user.id+fileExtension);
         user.profileThumbPicture("thumbprofile"+user.id+fileExtension);

         String keyToFetch =  "profile"+user.id+fileExtension;

         //----Copy over the image object
         S3Object object = s3.getObject(
                 new GetObjectRequest("globafitnessphotos", keyToFetch));
         InputStream objectData = object.getObjectContent();
         // Process the objectData stream.
         File f=new File("outFile.java");
         OutputStream out=new FileOutputStream(f);
         byte buf[]=new byte[1024];
         int len;
         while((len=objectData.read(buf))>0)
             out.write(buf,0,len);
         out.close();

         objectData.close();

         BufferedImage image2 = ImageIO.read(f);
         int height2 = image2.getHeight();
         int width2 = image2.getWidth();
         File resizedPhoto2 = new File(f.getName());
         resizedPhoto2.createNewFile();
         if((width2>=180) || height2>=120){
             resize(f,resizedPhoto2,50,50,true);
             f = resizedPhoto2;
         }
         s3.putObject(new PutObjectRequest("globafitnessphotos", "thumbprofile"+user.id+fileExtension, f).withCannedAcl(CannedAccessControlList.PublicRead));
         flash.success("You have successfully updated your profile photo");
         Settings.index();
     }

     public static void display_pictures(Long albumID){
        Album albums = Album.findById(albumID);
        render(albums);
     }

     public static void saveAlbum(Long albumID){
         if (params.get("submit") != null) {
             flash.success("Album successfully added");
             flash.clear();
             Photos.index();
         }
         else if (params.get("cancel") !=null) {
             User user = connected();
             Album currentAlbum = Album.findById(albumID);
             currentAlbum.delete();
             flash.success("Album was cancelled.");
             Photos.index();
         }
     }

     public static void deleteAlbum(Long albumID){
         Album currentAlbum = Album.findById(albumID);
         currentAlbum.deletePictures();
         currentAlbum.delete();
         flash.success("Album was deleted.");
         Photos.index();
     }

     public static void loading(){
         render();
     }


     public static void addComment(Long pictureID, String comment){
         Picture currentPicture = Picture.findById(pictureID);
         User user = connected();
         Picture_comment newComment = new Picture_comment(currentPicture, user, comment).save();
         newComment.done = true;
         JSONSerializer modelSerializer = new JSONSerializer().include("id","content","done").exclude("*");
         renderJSON(modelSerializer.serialize(newComment));
     }

     public static void deleteComment(Long commentID){
            Picture_comment newComment = Picture_comment.findById(commentID);
            newComment.delete();
     }

     public static void manage(){
         User user =connected();
         List<Album> myAlbumList = Album.find(
                 "select a from Album a where a.author = ? order by creationDate desc",user).fetch(10);
         render(user, myAlbumList);
     }

     public static void upload(String qqfile, Long albumID){
         if(request.isNew) {

             //FileOutputStream moveTo = null;

             Logger.info("Name of the file %s", qqfile);
             // Another way I used to grab the name of the file
             //String filename = request.headers.get("x-file-name").value();

             Logger.info("Absolute on where to send %s", Play.getFile("").getAbsolutePath() + File.separator + "uploads" + File.separator);


             try {

                 String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
                 String fileExtension = "";
                 for(String name : allowedExtensions){
                     int index = qqfile.indexOf(name);
                     if(index!=-1){
                         fileExtension=name;
                     }
                 }

                 InputStream data = request.body;

                 int maxwidth=600;
                 int maxheight=440;

                 String accessKey = "AKIAIIDVPNAYFEVBVVFA";
                 String secretKey = "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";
                 String myBucket = "globafitnessphotos";
                 AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

                 InputStream objectData = data;
                 // Process the objectData stream.
                 File f=new File("outFile.java");
                 OutputStream out=new FileOutputStream(f);
                 byte buf[]=new byte[1024];
                 int len;
                 while((len=objectData.read(buf))>0)
                     out.write(buf,0,len);
                 out.close();

                 objectData.close();

                 BufferedImage image = ImageIO.read(f);
                 int height = image.getHeight();
                 int width = image.getWidth();
                 File resizedPhoto = new File(f.getName());
                 resizedPhoto.createNewFile();
                 if((width>=maxwidth) || height>=maxheight){
                     resize(f,resizedPhoto,maxwidth,maxheight,true);
                     f = resizedPhoto;
                 }
                 /*String mimeType = play.libs.MimeTypes.getMimeType(f.getAbsolutePath());
                 String fileExtension = "";
                 if(mimeType.equals("image/jpeg")) fileExtension=".jpg";
                 else if(mimeType.equals("image/gif")) fileExtension=".gif";
                 else if(mimeType.equals("image/png")) fileExtension=".png";
                 else
                     renderJSON("{\"success\": " + mimeType +"}");*/
                 Album currentAlbum = Album.findById(albumID);
                 String amazonkey = currentAlbum.getNewKey(fileExtension);
                 String amazonThumbnailKey = currentAlbum.getNewThumbnailKey(fileExtension);
                 Picture newPicture = currentAlbum.addPicture("", "", fileExtension);

                 AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
                 TransferManager tx = new TransferManager(myCredentials);
                 PutObjectRequest req = new PutObjectRequest(myBucket, amazonkey, f);
                 req.setCannedAcl(CannedAccessControlList.PublicRead);

                 Upload myUpload = tx.upload(req);
                 while (myUpload.isDone() == false) {
                     System.out.println("Transfer: " + myUpload.getDescription());
                     System.out.println("  - State: " + myUpload.getState());
                     System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
                     // Do work while we wait for our upload to complete...
                     Thread.sleep(500);
                 }

                 makeAmazonThumb(amazonThumbnailKey, amazonkey, 180, 120);

             } catch(Exception ex) {

                 // catch file exception
                 // catch IO Exception later on
                 renderJSON("{success: false}");
             }

         }
         renderJSON("{success: true}");
     }

     public static void makeAmazonThumb(String amazonKey, String keyToFetch, Integer maxwidth, Integer maxheight) throws IOException {
         try {
             String accessKey = "AKIAIIDVPNAYFEVBVVFA";
             String secretKey = "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";
             String myBucket = "globafitnessphotos";
             AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
             S3Object object = s3.getObject(
                     new GetObjectRequest("globafitnessphotos", keyToFetch));
             InputStream objectData = object.getObjectContent();
             // Process the objectData stream.
             File file=new File("outFile2.java");
             OutputStream out=new FileOutputStream(file);
             byte buf[]=new byte[1024];
             int len;
             while((len=objectData.read(buf))>0)
                 out.write(buf,0,len);
             out.close();

             objectData.close();

             BufferedImage image = ImageIO.read(file);
             int height = image.getHeight();
             int width = image.getWidth();
             File resizedPhoto = new File(file.getName());
             resizedPhoto.createNewFile();
             if((width>=maxwidth) || height>=maxheight){
                 resize(file,resizedPhoto,maxwidth,maxheight,true);
                 file = resizedPhoto;
             }

             AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
             TransferManager tx = new TransferManager(myCredentials);
             PutObjectRequest req = new PutObjectRequest(myBucket, amazonKey, file);
             req.setCannedAcl(CannedAccessControlList.PublicRead);

             Upload myUpload = tx.upload(req);
             while (myUpload.isDone() == false) {
                 System.out.println("Transfer: " + myUpload.getDescription());
                 System.out.println("  - State: " + myUpload.getState());
                 System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
                 // Do work while we wait for our upload to complete...
                 Thread.sleep(500);
             }
         } catch(Exception ex) {

             // catch file exception
             // catch IO Exception later on
             renderJSON("{success: false}");
         }
         renderJSON("{success: true}");
     }

     public static void savAlbumTitle(Long id, String content) {
         Album currentAlbum = null;
         try {
             currentAlbum = Album.findById(id);
             currentAlbum.title = content;
             currentAlbum.save();
         } catch (Exception ex) {
             renderJSON("{success: false}");
         }
         JSONSerializer modelSerializer = new JSONSerializer().include("id", "title").exclude("*");
         renderJSON(modelSerializer.serialize(currentAlbum));
     }
 }


