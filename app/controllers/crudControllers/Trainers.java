package controllers.crudControllers;
import controllers.Application;
import controllers.CRUD;
import models.User;
import play.mvc.Before;

public class Trainers extends CRUD {

    @Before
    static void addUser() {
        User user = connected();
        if(user != null && user.isAdmin==true) {
            renderArgs.put("user", user);
        }else{
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
}