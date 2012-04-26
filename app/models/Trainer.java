package models;

import play.db.jpa.*;
import play.data.validation.*;
import javax.persistence.*;

@Entity
public class Trainer extends Model {
    
	public String gender;
	
	@Required(message="Phone Number is required")
	public String phoneNumber;
	
	@Required(message="Date of Birth is required")
	public String dateOfBirth;
	
	public String certifications;
	
	@Required(message="SSN number is required")
	@MinSize(value=9, message="SSN number must be 9 numbers")
	public String ssn;
	   
	@Required(message = "Address is required!")
	public String address;
	
	@Required(message = "City is required!")
	public String city;
	
	@Required(message = "State is required!")
	@MinSize(value=2, message="State must be 2 letters")
	public String state;
	
	@OneToOne
	public User user;
   
    @Required(message = "Zip code is required!")
    @MaxSize(value = 6, message="Zip must be less than 7 numbers")
    @MinSize(value = 5, message = "Zip code must be 5 numbers")
    public String zip;

	public Trainer(String gender, String zip, String dateOfBirth, String certifications, String ssn, User user, String address, String state, String city) {
		this.gender = gender;
		this.zip = zip;
		this.dateOfBirth = dateOfBirth;
		this.certifications = certifications;
		this.ssn = ssn;
		this.user = user;
		this.address = address;
		this.state = state;
		this.city = city;
    }

    public String toString() {
        return this.user.firstName + " " + this.user.lastName;
    }
    
}
