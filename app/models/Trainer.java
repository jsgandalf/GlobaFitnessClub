package models;

import play.db.jpa.*;
import play.data.validation.*;
import javax.persistence.*;

@Entity
public class Trainer extends Model {
    
	public String certifications;
	
	@Required(message="SSN number is required")
	@MinSize(value=9, message="SSN number must be 9 numbers")
	public String ssn;
	  	
	@OneToOne
	public User user;
   
  	public Trainer(String certifications, String ssn, User user) {
		this.certifications = certifications;
		this.ssn = ssn;
		this.user = user;
    }

    public String toString() {
        return this.user.firstName + " " + this.user.lastName;
    }
    
}
