 package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;

import java.util.*;

import models.*;

public class Trainers extends Controller {
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
	
    /*@Before
    static void checkUser() {
        if(connected() == null) {
            flash.error("Please log in first");
            Application.index();
        }
    }*/
    
    // ~~~
	
	public static void register(){
		render();
	}
	
    public static void index() {
        //List<Trainer> trainer_results = Trainer.find("byUser", connected()).fetch();
        //render(trainer_results);
		if(connected() != null) {
     	   render();
        }
		flash.error("Please log in first to view trainers.");
		Application.login_page();
    }

    public static void list(String search, Integer size, Integer page) {
        List<Trainer> trainers = null;
        page = page != null ? page : 1;
        if(search.trim().length() == 0) {
            //trainers = Trainer.all().fetch(page, size);
        } else {
            search = search.toLowerCase();
           trainers = Trainer.find("lower(zip) like ? OR lower(firstName) like ?", "%"+search+"%", "%"+search+"%").fetch(page, size);
			//trainers = Trainer.find("lower(name) like ? OR lower(city) like ?", "%"+search+"%", "%"+search+"%").fetch(page, size);
        }
        render(trainers, search, size, page);
    }
    
    public static void show(Long id) {
        Trainer trainer = Trainer.findById(id);
        render(trainer);
    }
        
    
    public static void settings() {
        render();
    }
    
	public static void result(){
		render();
	}
	
    public static void saveSettings(String password, String verifyPassword) {
        User connected = connected();
        connected.password = password;
        validation.valid(connected);
        validation.required(verifyPassword);
        validation.equals(verifyPassword, password).message("Your password doesn't match");
        if(validation.hasErrors()) {
            render("@settings", connected, verifyPassword);
        }
        connected.save();
        flash.success("Password updated");
        index();
    }
    
    public static void saveTrainer(@Valid Trainer trainer) {
		validation.email(trainer.email);
		if(validation.hasErrors()) {
            render("@Trainers.register", trainer);
        }
        trainer.create();
        session.put("trainer", trainer.email);
        flash.success("Welcome, " + trainer.firstName);
        result();
    }
	public static void logout() {
        session.clear();
        index();
	}
	public static void home(){
		Application.index();
	}
}

