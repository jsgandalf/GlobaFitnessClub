package models;

import org.eclipse.jdt.core.dom.ThisExpression;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class user_fitnessgoal extends Model {

    @ManyToOne
    @Required
    public User author;

    @Required
    public String name;

    @Required
    public Boolean value;

    public user_fitnessgoal(User author, String name, boolean value) {
        this.author = author;
        this.name = name;
        this.value = value;
        this.create();
    }

    //Not a very good way of doing this, but this will have to do.
    //Need every User to have a list of fitnessGoals, we are going to pull the names of the
    //fitness goals in teh FItnessGoal table.
    public static void newUserSetupForGoals(User author){
        List<FitnessGoal> goal = FitnessGoal.findAll();
        Iterator<FitnessGoal> iterator = goal.iterator();
        while (iterator.hasNext()) {
            user_fitnessgoal goals = new user_fitnessgoal(author, iterator.next().name, false);
        }
    }

    //This always sets the goal to true
    public static void setUserFitnessGoal(Long fitnessGoalID, boolean value){
        user_fitnessgoal myGoal = user_fitnessgoal.findById(fitnessGoalID);
        myGoal.value = value;
        myGoal.save();
    }

   public String toString()  {
        return author.fullName()+"=>"+name;
   }
}
