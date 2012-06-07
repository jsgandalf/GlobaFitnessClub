package controllers;

import models.CalendarEvent;
import models.User;
import models.user_fitnessgoal;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import security.BCrypt;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static models.user_fitnessgoal.setUserFitnessGoal;

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


	public static void editProfile(){
		User user = connected();
        List<user_fitnessgoal> fitnessGoals = user_fitnessgoal.find(
            "select f from user_fitnessgoal f where f.author = ? order by id asc",user).fetch();
        render(user, fitnessGoals);
	}

	public static void saveProfile(User user, Set<Long> fitnessGoal){
		User myUser = connected();
        List<user_fitnessgoal> myGoals = user_fitnessgoal.find("byAuthor",myUser).fetch();
        if(fitnessGoal==null || fitnessGoal.size()==0){
            for(user_fitnessgoal goal : myGoals){
                setUserFitnessGoal(goal.id,false);
            }
        }else{
            for(user_fitnessgoal goal : myGoals){
                if(fitnessGoal.contains(goal.id))
                    setUserFitnessGoal(goal.id,true);
                else
                    setUserFitnessGoal(goal.id,false);
            }
        }
        myUser.city = user.city;
		myUser.state = user.state;
		myUser.email = user.email;
		myUser.phoneNumber = user.phoneNumber;
		myUser.bio = user.bio;
        myUser.relationship = user.relationship;
		myUser.save();
		index();
	}


	public static void index(){
		User user = connected();
        session.put("isHome","false");
        List<user_fitnessgoal> fitnessGoals = user_fitnessgoal.find(
                "select f from user_fitnessgoal f where f.author = ? and f.value = 1 order by id asc",user).fetch();
        List<CalendarEvent> calendarList = CalendarEvent.find("byAuthor",user).fetch();
        render(user, fitnessGoals, calendarList);
	}
	public static void saveSettings(@Required(message="Please type in your current password")String userPassword, @Required(message="Please provide your new password")String newPassword) {
		User user = connected();
		validation.required(userPassword);
		validation.required(newPassword);
		if(validation.hasErrors()) {
			render("@settings", userPassword, newPassword);
		}
        BCrypt B = new BCrypt();
        if(B.checkpw(userPassword, user.password)) {
            user.password = B.hashpw(newPassword, B.gensalt(12));
            user.save();
            flash.error("");
            flash.success("Password Reset Successfully");
            render("@settings");
        }else{
			flash.error("Current Password Incorrect");
			render("@settings", userPassword, newPassword);
		}
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