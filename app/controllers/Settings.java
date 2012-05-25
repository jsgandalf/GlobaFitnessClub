package controllers;

import models.Album;
import models.Post;
import models.User;
import models.user_fitnessgoal;
import notifiers.Mails;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;
import security.BCrypt;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static models.user_fitnessgoal.newUserSetupForGoals;
import static models.user_fitnessgoal.setUserFitnessGoal;

public class Settings extends Controller{

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
        List<user_fitnessgoal> fitnessGoals = user_fitnessgoal.find(
                "select f from user_fitnessgoal f where f.author = ? and f.value = 1 order by id asc",user).fetch();
        render(user, fitnessGoals);
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

    //Another feature to make would be to just make users inactive
    public static void delete(){
        User user = connected();
        if(user!= null){
            session.clear();
            List<user_fitnessgoal> user_fitnessgoalList = user_fitnessgoal.find("byAuthor",user).fetch();
            Iterator<user_fitnessgoal> iterator = user_fitnessgoalList.iterator();
            while (iterator.hasNext()) {
                iterator.next().delete();
            }
            List<Album> albums = Album.find("byAuthor",user).fetch();
            Iterator<Album> iterator2 = albums.iterator();
            while (iterator2.hasNext()) {
                Album myAlbum = iterator2.next();
                myAlbum.deletePictures();
                myAlbum.delete();
            }
            List<Post> myPosts = Post.find("byAuthor",user).fetch();
            Iterator<Post> iterator3 = myPosts.iterator();
            while (iterator3.hasNext()) {
                iterator3.next().delete();
            }

            user.delete();
        }
        session.clear();
        render();
    }
}