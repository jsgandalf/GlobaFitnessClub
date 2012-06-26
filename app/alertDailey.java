import models.CalendarEvents;
import notifiers.Mails;
import play.jobs.Job;
import play.jobs.On;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Fire every 15 minutes starting from 0 hours (12am) every day
@On("0 0 0 * * ?")
public class alertDailey extends Job {

    public void doJob() {
       int reminderMinute = 1440;
       //Date date = new Date();
       String DATE_FORMAT = "yyyy-MM-dd hh:mm";
       SimpleDateFormat sdf =
                new SimpleDateFormat(DATE_FORMAT);
       Calendar cal = Calendar.getInstance();
       // Logger.info("System check, current time: %s\n", sdf.format(cal.getTime()));
       //Logger.info("Current Time at Job: %s\n", sdf.format(cal.getTime()));
       Date date = cal.getTime();
       Calendar cal2 = Calendar.getInstance();
       cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)+23);
       Date date2 = cal.getTime();
       List<CalendarEvents> events = CalendarEvents.find(
               "select e from CalendarEvents e where e.start > ? and e.start<=? and e.emailReminder = ?",date,date2,reminderMinute).fetch();
       for(CalendarEvents event : events) {
            String startAmPm = new SimpleDateFormat("aa").format(event.start);
            String endAmPm = new SimpleDateFormat("aa").format(event.end);
            Mails.reminder(event, startAmPm, endAmPm);
       }
    }

}