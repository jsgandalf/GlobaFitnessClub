package controllers;

import models.FitnessGoal;
import models.Post;
import models.User;
import models.user_fitnessgoal;
import notifiers.Mails;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.GenericModel;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;
import security.BCrypt;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static models.user_fitnessgoal.newUserSetupForGoals;
import static models.user_fitnessgoal.setUserFitnessGoal;

public class Application extends Controller{

    @Before
    static void addUser() {
        User user = connected();
        if(user != null  && (user.isAdmin==true)) {
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
		User user = connected();
		render(user);
	}

    public static void saveUser(@Valid User user, @Required(message="Re-enter Your password") String verifyPassword,
								@Required(message="Re-enter your email") String verifyEmail) {

        validation.required(verifyPassword);
        validation.required(verifyEmail);
        validation.equals(verifyEmail, user.email).message("Your email doesn't match");
        validation.equals(verifyPassword, user.password).message("Your password doesn't match");
		User myUser = User.find("byEmail", user.email).first();
        if(validation.hasErrors()) {
            render("@register", user, verifyPassword, verifyEmail);
        }
        if(myUser == null){
			Mails.welcome(user);
			flash.success("Your account is created.");
			BCrypt B = new BCrypt();
			user.password = B.hashpw(user.password, B.gensalt(12));
            user.create();
            newUserSetupForGoals(user);
            flash.success("Welcome, " + user.firstName);
			session.put("user", user.email);
			profile();
		}else{
			flash.error("Someone is already registered with that email");
			render("@register", user, verifyPassword, verifyEmail);
		}
    }

    public static void profile(){
        User user = connected();
        if(user==null){
            flash.error("You must login to see your profile");
            Application.login_page();
        }
        render(user);
    }

    public static void login(String email, String password) {
		if(!email.equals("") && !password.equals("")){
			User user = User.find("byEmail", email).first();
			if(user!=null){
				BCrypt B = new BCrypt();
				if(B.checkpw(password, user.password)) {
					session.put("user", user.email);
					flash.success("Welcome, " + user.firstName);
					profile();
				}
			}
		}
		// Oops
		flash.put("email", email);
		flash.error("Login failed");
		render("@login_page");
    }

    public static void logout() {
        session.clear();
		flash.success("Thank you for using Globa Fitness!");
        index();
    }
    public static void register() {
        flash.clear();
        render();
    }

	public static void login_page(){
		render();
	}

	public static void contact(){
		User user = connected();
        render(user);
	}

	public static void consultation(){
        User user = connected();
        render(user);
	}

	public static void siteDown(){
        User user = connected();
        render(user);
    }

	public static void weightLoss(){
        User user = connected();
        render(user);
    }

	public static void bodyBuilding(){
        User user = connected();
        render(user);
	}
	public static void functionalTraining(){
        User user = connected();
        render(user);
	}
	public static void highIntensityIntervalTraining(){
        User user = connected();
        render(user);
    }
	public static void goalSpecificTraining(){
        User user = connected();
        render(user);
    }
    public static void personalizedNutritionPlan(){
        User user = connected();
        render(user);
    }
	public static void postInjury(){
        User user = connected();
        render(user);
    }
	public static void sportsConditioning(){
        User user = connected();
        render(user);
    }
	public static void privateTraining(){
		siteDown();
	}
	public static void partnerTraining(){
		siteDown();
	}
	public static void groupTraining(){
		siteDown();
	}
	public static void members(){
		siteDown();
	}
	public static void network(){
		siteDown();
	}

	public static void sendContactForm(@Required(message="Please put in your first name.")String firstName, @Required(message="Please type in your last name.")String lastName, @Required(message="Please type in your email.")String email,@Required(message="Please type in the subject.")String subject, @Required(message="Please put in your message.")String message){
		validation.required(firstName);
		validation.required(lastName);
		validation.required(email);
		validation.required(subject);
		validation.required(message);
		if(validation.hasErrors()) {
			render("@contact", firstName, lastName, email, subject, message);
		}
		flash.success("Your message has been sent.");
		Mails.contact(firstName, lastName, email, subject, message);
		User user = connected();
        render("@contact", user);
	}

	public static void sendConsultationForm(String firstName, String lastName, String email, String phoneNumber, String question1, String question2, String question3,String question4,String question5,String question6,String question7,String question8){
		validation.required(firstName).message("First name is required.");
		validation.required(lastName).message("Last name is required.");
		validation.required(email).message("Email is required.");
		validation.required(question1).message("Question 1 is required.");
		validation.required(question3).message("Question 3 is required.");
		validation.required(question4).message("Question 4 is required.");
		validation.required(question5).message("Question 5 is required.");
		validation.required(question7).message("Question 7 is required.");
		validation.required(question8).message("Question 8 is required.");
		if(validation.hasErrors()) {
			render("@consultation",firstName, lastName,  email,  phoneNumber,  question1,  question2,  question3, question4, question5, question6, question7, question8);
		}
		flash.success("Your free consultation has been submitted.  Please allow up to 24 hours for a representative to contact you.");
		Mails.consultation(firstName, lastName,  email,  phoneNumber,  question1,  question2,  question3, question4, question5, question6, question7, question8);
		User user = connected();
        render("@consultation",user);
	}

	public static void posts(){
		User user = connected();
		Post frontPost = Post.find(
    		"select p from Post p where p.author = ? order by postedAt desc",user).first();
		List<Post> olderPosts = Post.find(
    		"select p from Post p where p.author = ? order by postedAt desc",user).from(1).fetch(10);
        render(user, frontPost, olderPosts);
	}

	public static void showPost(Long id) {
        Post post = Post.findById(id);
        String randomID = Codec.UUID();
		User user = connected();
		render(user, post, randomID);
    }
	public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#E4EAFD");
        Cache.set(id, code, "30mn");
        renderBinary(captcha);
    }
	public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }

	public static void postComment(
			Long postId,
			@Required(message="Author is required") String author,
			@Required(message="A message is required") String content,
			@Required(message="Please type the code") String code,
			String randomID)
	{
		Post post = Post.findById(postId);
		validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
		if(validation.hasErrors()) {
			render("@showPost", post, randomID);
		}
		post.addComment(author, content);
		flash.success("Thanks for posting %s", author);
		Cache.delete(randomID);
		showPost(postId);
	}

	public static void createPost(){
		User user = connected();
		render(user);
	}

	public static void createPostFunction(@Required(message="Title is required") String post_title, @Required(message="Content is required") String post_content)
	{
		if(validation.hasErrors()) {
			render("@createPost", post_title, post_content);
		}
		Post post = new Post(connected(),post_title,post_content);
		post.create();
		posts();
	}

	public static void thankyou(){
        User user = connected();
        render(user);
    }
	public static void donate(){
        User user = connected();
        render(user);
    }
    public static void resetPassword(){
        User user = connected();
        render(user);
    }
    public static void resetPasswordFunction(@Required(message="Please type in your email to reset your password.")String email, @Required(message="Please type in your zip code.")String zip)	{
        validation.required(email);
        validation.required(zip);
        flash.put("email", email);
        flash.put("zip", zip);
        User user = User.find("byEmail", email).first();
        if(validation.hasErrors()) {
            render("@resetPassword", email, zip);
        }else if(user != null && ((user.zip).equals(zip)||user.zip==null)) {
            Mails.lostPassword(user);
            flash.success("Password Changed Successfully. Please check your email for your password.");
            render("@resetPassword");
        }else if(user!= null && !(user.zip).equals(zip)){
            flash.error("Zip code incorect.");
            render("@resetPassword", email, zip);
        }
        // No matching email or zip
        flash.error("There is no one registered with that email.");
        render("@resetPassword", email, zip);
    }
}