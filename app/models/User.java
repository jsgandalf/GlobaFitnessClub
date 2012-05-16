package models;

import play.db.jpa.*;
import play.data.validation.*;
import javax.persistence.*;
import play.db.jpa.Blob;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends Model {
	
	public String gender;
	public String phoneNumber;
	public String dateOfBirth;
	public String address;
	public String city;
	
	public Blob photo;
	
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
   
    @Lob
    public String bio;
   
    public boolean isAdmin;

    @OneToMany(cascade=CascadeType.ALL)
    public List<Album> myAlbums;

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
		this.isAdmin = false;
        this.myAlbums = new ArrayList<Album>();
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

    public Album addAlbum(){
        Album newAlbum = new Album(this,"","");
        this.myAlbums.add(newAlbum);
        this.save();
        return newAlbum;
    }
    public Album addAlbum(String title, String description){
        Album newAlbum = new Album(this,title, description);
        this.myAlbums.add(newAlbum);
        this.save();
        return newAlbum;
    }
    public Album getLastAlbum(){
        //If there are not any albums available then make your first one
        if(myAlbums.isEmpty()==true){
            this.addAlbum();
        }
        return myAlbums.get(myAlbums.size() - 1);
    }
    public void deleteLastAlbum(){
        Album lastAlbum = getLastAlbum();
        lastAlbum.deletePictures();
        //lastAlbum.delete();
    }
}
