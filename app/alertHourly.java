import models.CalendarEvents;
import notifiers.Mails;
import play.jobs.Job;
import play.jobs.On;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Fire every 15 minutes starting from 0 hours (12am) every day
@On("0 0 * * * ?")
public class alertHourly extends Job {

    public void doJob() {
       int reminderMinute = 60;
       String DATE_FORMAT = "yyyy-MM-dd hh:mm";
       SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
       Calendar cal = Calendar.getInstance();
       cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+reminderMinute);
       Date date = cal.getTime();
       List<CalendarEvents> events = CalendarEvents.find(
               "select e from CalendarEvents e where e.start = ? and e.emailReminder = ?",date,reminderMinute).fetch();
       for(CalendarEvents event : events) {
            String startAmPm = new SimpleDateFormat("aa").format(event.start);
            String endAmPm = new SimpleDateFormat("aa").format(event.end);
            Mails.reminder(event, startAmPm, endAmPm);
       }
    }

}