package controllers;

import flexjson.JSONSerializer;
import models.*;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Calendar;
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
        int size = 8;
        List<user_fitnessgoal> fitnessGoals = user_fitnessgoal.find(
                "select f from user_fitnessgoal f where f.author = ? and f.value = 1 order by id asc",user).fetch();
        List<Share> shares = Share.find(
                "select s from Share s where s.author = ? order by creationDate desc",user).from(0).fetch(size);
        List<Share> totalShares = Share.find(
                "select s from Share s where s.author = ? order by creationDate desc",user).fetch();
        //Add 1 to it becuase its not counting the index
        int total = (totalShares.size()/size);
        int mod = totalShares.size()%size;
        if(mod>0)
            total++;
        render(user, fitnessGoals, shares, total);
	}

    public static void calendar(){
        User user = connected();
        List<CalendarEvents> calendarList = CalendarEvents.find("byAuthor", user).fetch();
        render(user, calendarList);
    }

    public static void createNewEvent(String what, int start, int end, Date date1, Date date2){
        User user = connected();
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(date1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, start);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Long time = cal.getTimeInMillis();
        date1 = cal.getTime();

        cal.setTime(date2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, end);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        time = cal.getTimeInMillis();
        date2 = cal.getTime();

        CalendarEvents newEvent = new CalendarEvents(user,what,date1,date2);
        newEvent.save();
        String success = "\"success\"";
        //String success = "\"Failed to delete picture, please contact us about this issue and we will fix it as soon as possible.\"";
        renderJSON("{\"success\": " + success +"}");
    }

    public static void editEvent(Long id){
        User user = connected();
        CalendarEvents newEvents = CalendarEvents.findById(id);
        newEvents.delete();
        String success = "\"success\"";
        //String success = "\"Failed to delete picture, please contact us about this issue and we will fix it as soon as possible.\"";
        renderJSON("{\"success\": " + success +"}");
    }

    public static void moveEvent(Long id, int deltaDays, int deltaMinutes){
        CalendarEvents currentEvent = CalendarEvents.findById(id);
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(currentEvent.start);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + deltaDays);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + (deltaMinutes/60));
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        currentEvent.start = cal.getTime();

        Calendar cal2 = Calendar.getInstance(); // locale-specific
        cal2.setTime(currentEvent.end);
        cal2.set(Calendar.DAY_OF_MONTH, cal2.get(Calendar.DAY_OF_MONTH) + deltaDays);
        cal2.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY) + (deltaMinutes/60));
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        currentEvent.end = cal2.getTime();
        currentEvent.save();
        String success = "\""+currentEvent.end+"\"";
        String success2 = "\""+currentEvent.start+"\"";
        renderJSON("{\"start\": " + success +",\"end\": " + success2 +"}");
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

    public static void shareNews(int page){
        int size =8;
        if(page!=0)
            page--;
        User user = connected();
        int start = page * size;
        List<Share> shares = Share.find(
                "select s from Share s where s.author = ? order by creationDate desc",user).from(start).fetch(size);
        render(shares,user);
    }

    public static void Dialog_Event(Long id){
        CalendarEvents event = CalendarEvents.findById(id);
        render(event);
    }

    public static void deleteEvent(Long id){
        CalendarEvents event = CalendarEvents.findById(id);
        event.delete();
        String success = "\"success\"";
        renderJSON("{\"success\": " + success +"}");
    }

}