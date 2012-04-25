package models;

import play.db.jpa.*;
import play.data.validation.*;
import javax.persistence.*;
import java.math.*;

@Entity
public class Trainer extends Model {
    
	public String gender;
	
	@Required(message="Phone Number is required")
	public String phoneNumber;
	
	@Required(message="Date of Birth is required")
	public String dateOfBirth;
	
	public String certifications;
	
	@Required(message="SSN number is required")
	public String ssn;
	
	@Email
    @Required(message = "Email is required!")
    @MaxSize(50)
    @MinSize(4)
    public String email;
    
	public User user;
    
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

	public Trainer(String firstName, String lastName, String gender, String email, String zip, String dateOfBirth, String certifications, String ssn, User user) {
        this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.email = email;
		this.zip = zip;
		this.dateOfBirth = dateOfBirth;
		this.certifications = certifications;
		this.ssn = ssn;
		this.user = user;
		
    }

    public String toString() {
        return firstName + " " + lastName;
    }
    
}
