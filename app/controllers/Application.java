package controllers;

import play.mvc.*;
import play.data.validation.*;

import models.*;

public class Application extends Controller {
    
    @Before
    static void addUser() {
        User user = connected();
        if(user != null) {
            renderArgs.put("user", user);
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
    
    // ~~

    public static void index() {
        /*if(connected() != null) {
            Trainers.index();
        }*/
		render();
    }
    
	public static void home(){
		Application.index();
	}
	
    public static void saveUser(@Valid User user,
								@Required(message="Re-enter Your password") String verifyPassword, 
								@Required(message="Re-enter your email") String verifyEmail) {
        validation.required(verifyPassword);
		validation.required(verifyEmail);
		validation.equals(verifyEmail, user.email).message("Your email doesn't match");
        validation.equals(verifyPassword, user.password).message("Your password doesn't match");
        if(validation.hasErrors()) {
            render("@register", user, verifyPassword, verifyEmail);
        }
        user.create();
        session.put("user", user.email);
        flash.success("Welcome, " + user.firstName);
        index();
    }
    
    public static void login(String email, String password) {
		User user = User.find("byEmailAndPassword", email, password).first();
        if(user != null) {
            session.put("user", user.email);
            flash.success("Welcome, " + user.firstName);
            profile();         
        }
        // Oops
        flash.put("email", email);
        flash.error("Login failed");
        login_page();
    }
    
    public static void logout() {
        session.clear();
        index();
    }
    public static void register() {
        render();
    }
	
	public static void profile(){
		User user = connected();
		render(user);
	}
 
	public static void login_page(){
		render();
	}
	
	public static void contact(){
		render();
	}
	
	public static void consultation(){
		render();
	}
	
	public static void editProfile(){
		render();
	}
	
	public static void saveProfile(User user){
		User myUser = connected();
		myUser.city = user.city;
		myUser.state = user.state;
		myUser.email = user.email;
		myUser.save();
		profile();
	}
}