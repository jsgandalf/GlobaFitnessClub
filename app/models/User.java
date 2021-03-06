package models;

import play.data.validation.*;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class User extends Model {
	
	public String gender;
	public String phoneNumber;
	public String dateOfBirth;
	public String address;
	public String city;
	
	@MinSize(value=2, message="State must be 2 letters")
	public String state;
	
	@Email
    @Required(message = "Email is required!")
    @MaxSize(100)
    @MinSize(4)
    public String email;

    @Required(message = "Password is required")
    @MinSize(5)
    public String password;
    
    @Required(message = "First name is required!")
    @MaxSize(100)
    @Match(value="^\\w*$", message="First name cannot contain special characters")
    public String firstName;
	
	@Required(message = "Last name is required!")
    @Match(value="^\\w*$", message="Last name cannot contain special characters")
    @MaxSize(100)
    public String lastName;
   
    @Required(message = "Zip code is required!")
    @MaxSize(value = 6, message="Zip must be less than 7 numbers")
    @MinSize(value = 5, message = "Zip code must be 5 numbers")
    public String zip;

    public String relationship;

    public String profilePic;
    public String profileThumbPic;

    @Lob
    public String bio;
   
    public boolean isAdmin;

    public User(String firstName, String lastName, String password, String email, String zip, String address, String state, String city,String gender, String dateOfBirth, String phoneNumber, String bio, String relationship) {
        this.firstName = firstName;
		this.lastName = lastName;
        this.password = password;
		this.email = email;
		this.zip = zip;
		this.address = address;
		this.state = state;
		this.city = city;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.bio = bio;
		this.isAdmin = false;
        this.relationship = relationship;
        this.profilePic = null;
        this. profileThumbPic = null;
    }

    public String toString()  {
        return email+"-> "+firstName+" "+lastName;
    }
    
	public String fullName(){
		return firstName + " "+ lastName;
	}
	
	public static boolean isEmailAvailable(String email) {
        return User.find("byEmail") == null;
    }

    public String profilePicture(String profile){
        this.profilePic = profile;
        this.save();
        return this.profilePic;
    }

    public String getProfilePic(){
        if(this.profilePic != null && !this.profilePic.isEmpty() && !this.profilePic.trim().isEmpty())
            return "https://s3.amazonaws.com/globafitnessphotos/"+this.profilePic;
        else if(this.gender!=null)
            if(this.gender.equals("female") || this.gender.equals("Female"))
                return "https://s3.amazonaws.com/globafitnessphotos/default/female.jpg";
        return "https://s3.amazonaws.com/globafitnessphotos/default/default.jpg";
    }

    public String profileThumbPicture(String profile){
        this.profileThumbPic = profile;
        this.save();
        return this.profileThumbPic;
    }

    public String getProfileThumbPic(){
        if(this.profileThumbPic != null && !this.profileThumbPic.isEmpty() && !this.profileThumbPic.trim().isEmpty())
            return "https://s3.amazonaws.com/globafitnessphotos/"+this.profileThumbPic;
        else if(this.gender!=null)
            if(this.gender.equals("female") || this.gender.equals("Female"))
                return "https://s3.amazonaws.com/globafitnessphotos/default/femaleThumb.jpg";
        return "https://s3.amazonaws.com/globafitnessphotos/default/defaultThumb.jpg";
    }

}
