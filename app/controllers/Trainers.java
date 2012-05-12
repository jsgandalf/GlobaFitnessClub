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
    
    // ~~~
	
	public static void register(){
		if(connected() != null) {
			Trainer trainer = new Trainer("","",connected());
     		render(trainer);
        }
		flash.error("Please log in first to join trainers network.");
		Application.login_page();
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
            trainers = Trainer.find("lower(user.zip) like ? OR lower(user.firstName) like ?", "%"+search+"%", "%"+search+"%").fetch(page, size);
			//trainers = Trainer.find("lower(name) like ? OR lower(city) like ?", "%"+search+"%", "%"+search+"%").fetch(page, size);
        }
        render(trainers, search, size, page);
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
		validation.required(trainer.user.address).message("Address is required.");
		validation.required(trainer.user.state).message("State is required.");
		validation.required(trainer.user.city).message("City is required.");
		validation.required(trainer.user.zip).message("Zip is required.");
		validation.required(trainer.user.phoneNumber).message("Phone number is required.");
		validation.required(trainer.user.dateOfBirth).message("Date of Birth is required.");
		validation.min(trainer.user.zip, 5).message("Zip must be 5 digits");
		validation.min(trainer.user.phoneNumber, 10).message("Phone number must be 10 digits (make sure to include your area code).");
		if(validation.hasErrors()) {
            render("@Trainers.register", trainer);
        }
		//Update users as well
		User user = connected();
		user.address = trainer.user.address;
		user.city = trainer.user.city;
		user.state = trainer.user.state;
		user.zip = trainer.user.zip;
		user.phoneNumber = trainer.user.phoneNumber;
		user.gender = trainer.user.gender;
		trainer.user = user;
		user.save();
        trainer.create();
        session.put("trainer", trainer.user.email);
        flash.success("Welcome, " + trainer.user.firstName);
        result();
    }
	public static void logout() {
        session.clear();
        index();
	}
	public static void home(){
		Application.index();
	}
	public static void show(Long id){
		Trainer trainer = Trainer.findById(id);
		render(trainer);
	}	
}

