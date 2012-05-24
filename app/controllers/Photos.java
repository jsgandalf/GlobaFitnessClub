 package controllers;

 import com.amazonaws.auth.AWSCredentials;
 import com.amazonaws.auth.BasicAWSCredentials;
 import com.amazonaws.services.s3.AmazonS3;
 import com.amazonaws.services.s3.AmazonS3Client;
 import com.amazonaws.services.s3.model.*;

 import com.amazonaws.services.s3.transfer.TransferManager;
 import com.amazonaws.services.s3.transfer.Upload;
 import com.ning.http.client.FilePart;
 import models.Album;
 import models.Picture;
 import models.User;
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
         if(connected() != null) {
             User user = connected();

             List<Album> myAlbumList = Album.find(
                     "select a from Album a where a.author = ? order by creationDate desc",user).fetch(10);
             render(user, myAlbumList);
         }
         flash.error("Please log in first to view photos.");
         Application.login_page();
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


     public static void deletePhoto(String key){
         String accessKey = "AKIAIIDVPNAYFEVBVVFA";
         String secretKey = "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";
         AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
         s3.deleteObject(new DeleteObjectRequest("globafitnessphotos",key));
     }


     public static void copyAmazonPicture(String amazonKey, String keyToFetch, Integer maxwidth, Integer maxheight, String renderString) throws IOException {
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
             f = resizedPhoto;;
         }
         s3.putObject(new PutObjectRequest("globafitnessphotos", amazonKey, f).withCannedAcl(CannedAccessControlList.PublicRead));
         if(renderString.equals("@Settings.index"))
            render("@Settings.index");
         else
             addAlbumPhotos(Long.valueOf(renderString));
     }

     public static void uploadPhotoToAlbum(String photo_title, String message, File photo, Long albumID) throws IOException {
         User user = connected();
         String accessKey = "AKIAIIDVPNAYFEVBVVFA";
         String secretKey = "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";
         if(photo==null){
             flash.error("Please upload a file");
             addAlbumPhotos(albumID, photo_title, message);
         }
         String mimeType = play.libs.MimeTypes.getMimeType(photo.getAbsolutePath());
         if(!mimeType.equals("image/jpeg") && !mimeType.equals("image/gif") && !mimeType.equals("image/png")){
             flash.error("File is not jpg, gif, or png. Please upload this type of file.");
             addAlbumPhotos(albumID, photo_title, message);
         }
         if(photo.length()>MAX_SIZE){
             flash.error("File is too large, must be less than 2.5mb");
             addAlbumPhotos(albumID, photo_title, message);
         }
         String fileExtension = "";
         if(mimeType.equals("image/jpeg")) fileExtension=".jpg";
         else if(mimeType.equals("image/gif")) fileExtension=".gif";
         else if(mimeType.equals("image/png")) fileExtension=".png";
         else{
             flash.error("File is not jpg, gif, or png. Please upload this type of file.");
             addAlbumPhotos(albumID);
         }

         BufferedImage image = ImageIO.read(photo);
         int height = image.getHeight();
         int width = image.getWidth();
         File resizedPhoto = new File(photo.getName());
         resizedPhoto.createNewFile();
         if((width>=800) || height>=640){
             resize(photo,resizedPhoto,800,640,true);
             photo = resizedPhoto;;
         }

         flash.clear();
         Album currentAlbum = Album.findById(albumID);
         String amazonkey = currentAlbum.getNewKey(fileExtension);
         String amazonThumbnailKey = currentAlbum.getNewThumbnailKey(fileExtension);
         currentAlbum.addPicture(photo_title, message, fileExtension);
         AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
         s3.putObject(new PutObjectRequest("globafitnessphotos", amazonkey, photo).withCannedAcl(CannedAccessControlList.PublicRead));
         copyAmazonPicture(amazonThumbnailKey, amazonkey, 180, 120, ""+albumID);
         flash.success("You have successfully uploaded a photo");
         addAlbumPhotos(albumID);
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
         render("@Settings.index",user);
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

    //For multiple uploads --> haven't implemented it yet, it was too difficult
     /*

     public static void uploadMultiplePhotoToAlbum(File fake) {
         User user = connected();
         List<FileUpload> files = (List<FileUpload>) request.args.get("__UPLOADS");
         if(files==null){
             flash.error("Must upload files for photo album");
             render("@Photos.addAlbum", user);
         }
         Iterator<FileUpload> myFile = files.iterator();
         while (myFile.hasNext()) {
             FileUpload file = myFile.next();
             if(file!=null){
                 String contentType = file.getContentType();
                 if(contentType.equals("image/jpeg") && contentType.equals("image/gif") && contentType.equals("image/png")){
                     flash.error("File is not jpg, gif, or png. Please upload this type of file.");
                     render("@Photos.addAlbum", user);
                 }
                 if(file.getSize()>MAX_SIZE){
                     flash.error("File is too large, must be less than 2.5mb");
                     render("@Photos.addAlbum", user, file);
                 }
                 flash.error("");
                 String accessKey = "AKIAIIDVPNAYFEVBVVFA";
                 String secretKey = "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";
                 AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
                 String amazonkey = user.getLastAlbum().getNewKey();
                 user.getLastAlbum().addPicture();
                 s3.putObject(new PutObjectRequest("globafitnessphotos", amazonkey, file.asFile()).withCannedAcl(CannedAccessControlList.PublicRead));
             }
         }
         flash.clear();
         flash.success("You have successfully uploaded the album: ");
         render("@Photos.index");
     }

     public static void upload(String qqfile){
         if(request.isNew) {

             //FileOutputStream moveTo = null;

             Logger.info("Name of the file %s", qqfile);
             // Another way I used to grab the name of the file
             //String filename = request.headers.get("x-file-name").value();

             Logger.info("Absolute on where to send %s", Play.getFile("").getAbsolutePath() + File.separator + "uploads" + File.separator);
             try {

                 InputStream data = request.body;

                 String accessKey = "AKIAIIDVPNAYFEVBVVFA";
                 String secretKey = "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";
                 String myBucket = "globafitnessphotos";
                 AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
                 User user = connected();
                 String amazonkey = user.getLastAlbum().getNewKey();
                 user.getLastAlbum().addPicture();
                 //s3.putObject(new PutObjectRequest(myBucket, amazonkey, data, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));

                 AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
                 TransferManager tx = new TransferManager(myCredentials);
                 Upload myUpload = tx.upload(myBucket, amazonkey, data, new ObjectMetadata());
                 while (myUpload.isDone() == false) {
                     System.out.println("Transfer: " + myUpload.getDescription());
                     System.out.println("  - State: " + myUpload.getState());
                     System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
                     // Do work while we wait for our upload to complete...
                     Thread.sleep(500);
                 }
                 //s3.putObject(new PutObjectRequest("globafitnessphotos", key, photo).withCannedAcl(CannedAccessControlList.PublicRead));

                 //moveTo = new FileOutputStream(new File(Play.getFile("").getAbsolutePath()) + File.separator + "uploads" + File.separator + filename );
                 //IOUtils.copy(data, moveTo);

             } catch(Exception ex) {

                 // catch file exception
                 // catch IO Exception later on
                 renderJSON("{success: false}");
             }

         }
         renderJSON("{success: true}");
     }


     public static void display_upload(){
        render();
     }*/
}


