 package controllers;

 import com.amazonaws.auth.AWSCredentials;
 import com.amazonaws.auth.BasicAWSCredentials;
 import com.amazonaws.services.s3.AmazonS3;
 import com.amazonaws.services.s3.AmazonS3Client;
 import com.amazonaws.services.s3.model.CannedAccessControlList;
 import com.amazonaws.services.s3.model.GetObjectRequest;
 import com.amazonaws.services.s3.model.PutObjectRequest;
 import com.amazonaws.services.s3.model.S3Object;
 import com.amazonaws.services.s3.transfer.TransferManager;
 import com.amazonaws.services.s3.transfer.Upload;
 import flexjson.JSONSerializer;
 import models.Album;
 import models.Picture;
 import models.Picture_comment;
 import models.User;
 import play.libs.Images;
 import play.mvc.Before;
 import play.mvc.Controller;

 import javax.imageio.ImageIO;
 import java.awt.image.BufferedImage;
 import java.io.*;
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
     private static long MAX_SIZE = 5242880;

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
         String success = "\"success\"";
         try{
            Picture myPicture = Picture.findById(id);
            //notFoundIfNull(myPicture);
            myPicture.deletePicture();
        } catch(Exception ex) {
            success = "\"Failed to delete picture, please contact us about this issue and we will fix it as soon as possible.\"";
            renderJSON("{\"success\": " + success +"}");
        }
        renderJSON("{\"success\": " + success+"}");
     }

     public static void uploadPhotoToAlbum(String photo_title, String message, File photo, Long albumID) throws IOException {
         User user = connected();
         String accessKey = "AKIAIIDVPNAYFEVBVVFA";
         String secretKey = "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";

         if(photo==null){
             flash.error("Please upload a file");
             addAlbumPhotos(albumID);
         }
         String mimeType = play.libs.MimeTypes.getMimeType(photo.getAbsolutePath());
         if(!mimeType.equals("image/jpeg") && !mimeType.equals("image/gif") && !mimeType.equals("image/png")){
             flash.error("File is not jpg, gif, or png. Please upload this type of file.");
             addAlbumPhotos(albumID);
         }
         if(photo.length()>MAX_SIZE){
             flash.error("File is too large, must be less than 5mb");
             addAlbumPhotos(albumID);
         }
         String fileExtension = ".jpg";
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

         int maxwidth = 760;
         int maxheight = 600;

         File newFile = new File("Foo"+fileExtension); // create random unique filename here

         if((width>=maxwidth) || height>=maxheight){
             Images.resize(photo, newFile, maxwidth, maxheight, true);
         }else
             newFile = photo;

         flash.clear();

         Album currentAlbum = Album.findById(albumID);
         String amazonkey = currentAlbum.getNewKey(fileExtension);
         String amazonThumbnailKey = currentAlbum.getNewThumbnailKey(fileExtension);
         Picture newPicture = currentAlbum.addPicture(photo_title, message, fileExtension);
         AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
         s3.putObject(new PutObjectRequest("globafitnessphotos", amazonkey, newFile).withCannedAcl(CannedAccessControlList.PublicRead));

         File newThumbnailFile = new File("Foo2"+fileExtension); // create random unique filename here

         if((width>=180) || height>=120){
             Images.resize(photo, newThumbnailFile, 180, 120, true);
         }else
             newThumbnailFile = newFile;

         s3.putObject(new PutObjectRequest("globafitnessphotos", amazonThumbnailKey, newThumbnailFile).withCannedAcl(CannedAccessControlList.PublicRead));

         addAlbumPhotos(albumID);
     }

     public static void uploadProfilePhoto(File photo) throws IOException {
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
         s3.putObject(new PutObjectRequest("globafitnessphotos", "profile"+user.id+fileExtension, newFile).withCannedAcl(CannedAccessControlList.PublicRead));
         user.profilePicture("profile"+user.id+fileExtension);
         user.profileThumbPicture("thumbprofile"+user.id+fileExtension);

         File newThumbnailFile = new File("Foo2"+fileExtension); // create random unique filename here

         if((width>=50) || height>=50){
             Images.resize(photo, newThumbnailFile, 50, 50, true);
         }else
             newThumbnailFile = newFile;

         s3.putObject(new PutObjectRequest("globafitnessphotos", "thumbprofile"+user.id+fileExtension, newThumbnailFile).withCannedAcl(CannedAccessControlList.PublicRead));

         flash.success("You have successfully updated your profile photo");
         Settings.index();
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
         notFoundIfNull(currentAlbum);
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


     //This is for multipleUploads, If mike needs this feature, he can contract it out to me later.
     /*public static void upload(String qqfile, Long albumID) throws InterruptedException, IOException {
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

                 int maxwidth=700;
                 int maxheight=540;

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


                 File newFile = new File("Foo"+fileExtension); // create random unique filename here
                 Images.resize(f, newFile, maxwidth, maxheight, true);



                /*double scalex = (double) targetWidth / sourceWidth;
               double scaley = (double) targetHeight / sourceHeight;
               double scale =  Math.min(scalex, scaley);

               Image scaledImage = image.getScaledInstance((int) (targetWidth * scale), (int) (targetHeight * scale), Image.SCALE_SMOOTH);
               int myWidth = (int) (targetWidth * scale);
               int myHeight = (int) (targetHeight * scale);*/
             //ImageInfo info = new ImageInfo();
                /*MagickImage magickPhoto = new MagickImage( new ImageInfo( qqfile ) );
                MagickImage bigger = magickPhoto.scaleImage(100, 100);
                bigger.setFileName("bigger.jpg");*/
                //bigger.writeImage(info);
                //File resizedPhoto = new File("bigger.jpg");
             /*BufferedImage resizedImage = new BufferedImage(myWidth, myHeight, image.getType());
             Graphics2D g = resizedImage.createGraphics();
             g.drawImage(image, 0, 0, myWidth, myHeight, null);
             g.dispose();
             g.setComposite(AlphaComposite.Src);

             g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                     RenderingHints.VALUE_INTERPOLATION_BILINEAR);
             g.setRenderingHint(RenderingHints.KEY_RENDERING,
                     RenderingHints.VALUE_RENDER_QUALITY);
             g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);
             */
                 //String newFileExtension = fileExtension.replace(".", "");
                 //File tmpFile = new File(qqfile);
                 //ImageIO.write(scaledImage, newFileExtension, tmpFile);

                 //BufferedImage image = ImageIO.read(f);
                 //int height = image.getHeight();
                 //int width = image.getWidth();
                 /*File resizedPhoto = new File(f.getName());
                 resizedPhoto.createNewFile();
                 if((width>=maxwidth) || height>=maxheight){
                     resize(f,resizedPhoto,maxwidth,maxheight,true);
                     //f = resizedPhoto;
                 } */
                 /*String mimeType = play.libs.MimeTypes.getMimeType(f.getAbsolutePath());
                 String fileExtension = "";
                 if(mimeType.equals("image/jpeg")) fileExtension=".jpg";
                 else if(mimeType.equals("image/gif")) fileExtension=".gif";
                 else if(mimeType.equals("image/png")) fileExtension=".png";
                 else
                     renderJSON("{\"success\": " + mimeType +"}");*/
                /*
                 Album currentAlbum = Album.findById(albumID);
                 String amazonkey = currentAlbum.getNewKey(fileExtension);
                 String amazonThumbnailKey = currentAlbum.getNewThumbnailKey(fileExtension);
                 Picture newPicture = currentAlbum.addPicture("", "", fileExtension);

                 AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
                 TransferManager tx = new TransferManager(myCredentials);
                 PutObjectRequest req = new PutObjectRequest(myBucket, amazonkey, newFile);
                 //PutObjectRequest req = new PutObjectRequest(myBucket, amazonkey, f);
                 req.setCannedAcl(CannedAccessControlList.PublicRead);

                 Upload myUpload = tx.upload(req);
                 while (myUpload.isDone() == false) {
                     System.out.println("Transfer: " + myUpload.getDescription());
                     System.out.println("  - State: " + myUpload.getState());
                     System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
                     // Do work while we wait for our upload to complete...
                     Thread.sleep(500);
                 }

                 //makeAmazonThumb(amazonThumbnailKey, amazonkey, 180, 120);

             } catch(Exception ex) {

                 // catch file exception
                 // catch IO Exception later on
                 renderJSON("{success: false}");
             }

         }
         renderJSON("{success: true}");
     } */

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


