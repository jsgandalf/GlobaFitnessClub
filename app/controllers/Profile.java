package controllers;

import flexjson.JSONSerializer;
import models.*;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Date;
import java.util.List;

public class Profile extends Controller{

    @Before
    static void addUser() {
        User user = connected();
        if(user != null) {
            renderArgs.put("user", user);
		}else{
            flash.error("You must log in to view your settings");
            Application.login_page();
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
        session.put("isHome","false");
        List<user_fitnessgoal> fitnessGoals = user_fitnessgoal.find(
                "select f from user_fitnessgoal f where f.author = ? and f.value = 1 order by id asc",user).fetch();
        List<Share> shares = Share.find(
                "select s from Share s where s.author = ? order by creationDate desc",user).fetch();

        render(user, fitnessGoals, shares);
	}

    public static void calendar(){
        User user = connected();
        List<CalendarEvent> calendarList = CalendarEvent.find("byAuthor",user).fetch();
        render(user, calendarList);
    }

    public static void createNewEvent(String timeFrom, String timeTo, String what, Date date){
        User user = connected();
        CalendarEvent newEvent = new CalendarEvent(user,timeFrom,timeTo,what,date);
        newEvent.save();
        String success = "\"success\"";
        //String success = "\"Failed to delete picture, please contact us about this issue and we will fix it as soon as possible.\"";
        renderJSON("{\"success\": " + success +"}");
    }

    public static void editEvent(Long id){
        User user = connected();
        CalendarEvent newEvent = CalendarEvent.findById(id);
        newEvent.delete();
        String success = "\"success\"";
        //String success = "\"Failed to delete picture, please contact us about this issue and we will fix it as soon as possible.\"";
        renderJSON("{\"success\": " + success +"}");
    }

    public static void share(String content){
        User user = connected();
        String success = "\"success\"";
        if(content.equals("")||content==null){
            success = "\"Please share your current status!\"";
            renderJSON("{\"success\": " + success +"}");
        }
        Share newShare = new Share(user,content,1);
        newShare.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(newShare));
    }

    public static void shareLink(String message, String link){
        User user = connected();
        String success = "\"success\"";
        Share newShare = new Share(user,message,2);
        newShare.link = link;
        newShare.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(newShare));
    }

    public static void sharePhoto(String message, String photo){
        User user = connected();
        String success = "\"success\"";
        Share newShare = new Share(user,message,3);
        newShare.link = "The photo will point to someones album";
        newShare.photo = photo;
        newShare.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(newShare));
    }

    public static void shareVideo(String message, String video){
        User user = connected();
        String success = "\"success\"";
        Share newShare = new Share(user,message,4);
        newShare.videoName = video.replaceAll("http://youtu.be/","http://www.youtube.com/embed/");
        newShare.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(newShare));
    }

    public static void sharePhotoDiv(){
        User user = connected();
        List<Album> myAlbumList = Album.find(
                "select a from Album a where a.author = ? order by creationDate desc",user).fetch(10);
        render(myAlbumList);
    }

    public static void shareAlbumPhotoDiv(Long albumID){
        User user = connected();
        Album currentAlbum = Album.findById(albumID);
        render(user, currentAlbum);
    }

    public static void Dialog_Photo(Long pictureID){
        User user = connected();
        Picture picture = Picture.findById(pictureID);
        render(user, picture);
    }

    public static void deleteShare(Long id){
        Share newShare = Share.findById(id);
        newShare.deleteShare();
    }

    public static void addComment(Long shareID, String content){
        User user = connected();
        Share currentShare = Share.findById(shareID);
        Share_comment comment = new Share_comment(currentShare, user, content);
        comment.save();
        JSONSerializer modelSerializer = new JSONSerializer().include("id","content").exclude("*");
        renderJSON(modelSerializer.serialize(comment));
    }

    public static void deleteComment(Long commentID){
        Share_comment newComment = Share_comment.findById(commentID);
        newComment.delete();
    }

    public static void Dialog_Create_Event(){
        render();
    }
}