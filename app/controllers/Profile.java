package controllers;

import models.CalendarEvent;
import models.User;
import models.user_fitnessgoal;
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
        List<CalendarEvent> calendarList = CalendarEvent.find("byAuthor",user).fetch();
        render(user, fitnessGoals, calendarList);
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

}