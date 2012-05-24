package models;

import play.data.validation.*;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class FitnessGoal extends Model {

    public String name;

    public FitnessGoal(String name) {
        this.name = name;
    }

    public void addFitnessGoal(String name){
        this.name = name;
        this.save();
    }

    //This is an inefficient way of doing things but will work for now.
    //A new user will need fitnessGoals setup


    public String toString()  {
        return name;
    }
}
