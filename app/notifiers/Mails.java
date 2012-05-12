package notifiers;
 
import play.*;
import play.mvc.*;
import java.util.*;
import models.*;
import java.util.Random;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.*;
import security.*;
public class Mails extends Mailer {
 
   public static void welcome(User user) {
      setSubject("Welcome %s", user.firstName);
      addRecipient(user.email);
      setFrom("Globa Fitness Club <support@globafitness.com>");
      /*EmailAttachment attachment = new EmailAttachment();
      attachment.setDescription("A pdf document");
      attachment.setPath(Play.getFile("rules.pdf").getPath());
      addAttachment(attachment);*/
      send(user);
   }
 
   public static void lostPassword(User user) {
      setFrom("Globa Fitness Club <support@globafitness.com>");
	  String newPassword = ""+rndChar()+rndChar()+rndChar()+rndChar()+rndChar()+rndChar();
	  BCrypt B = new BCrypt();
      user.password = B.hashpw(newPassword, B.gensalt(12));
	  user.save();
	  setSubject("Your password has been reset");
      addRecipient(user.email);
      send(user, newPassword);
   }
   
   public static void contact(String firstName,String lastName,String email,String subject,String message) {
      setFrom("Globa Fitness Club <support@globafitness.com>");
      setSubject("Contact message: %s", subject);
      addRecipient("support@globafitness.com");
      send(firstName, lastName, email, message);
   }
 	
	public static void consultation(String firstName, String lastName, String email, String phoneNumber, String question1, String question2, String question3,String question4,String question5,String question6,String question7,String question8){
	  setFrom("Globa Fitness Club <support@globafitness.com>");
      setSubject("Consultation message: %s", email);
      addRecipient("support@globafitness.com");
      send(firstName, lastName,  email,  phoneNumber,  question1,  question2,  question3, question4, question5, question6, question7, question8);
	}
	private static char rndChar () {
		int rnd = (int) (Math.random() * 52); // or use Random or whatever
		char base = (rnd < 26) ? 'A' : 'a';
		return (char) (base + rnd % 26);

	}

}