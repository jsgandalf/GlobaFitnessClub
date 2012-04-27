package models;

import play.db.jpa.*;
import play.data.validation.*;
import javax.persistence.*;

@Entity
@Table(name="Customer")
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
    @MaxSize(15)
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
   
    @Lob
    public String bio;
   
    public boolean isAdmin;   
   
    public User(String firstName, String lastName, String password, String email, String zip, String address, String state, String city,String gender, String dateOfBirth, String phoneNumber, String bio) {
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
    }

    public String toString()  {
        return "User(" + firstName + " "+ lastName +")";
    }
    
	public String fullName(){
		return firstName + " "+ lastName;
	}
	
}