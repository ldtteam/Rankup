package com.minecolonies.rankup.util;

import com.minecolonies.rankup.Rankup;
import org.spongepowered.api.text.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommonUtils
{
    public static Text toText(final String string)
    {
        return Text.of(string);
    }

    public static String timeDescript(int timeNeeded)
    {
        long day = TimeUnit.MINUTES.toDays(timeNeeded);
        long hours = TimeUnit.MINUTES.toHours(timeNeeded - TimeUnit.DAYS.toMinutes(day));
        long minutes = TimeUnit.MINUTES.toMinutes((timeNeeded - TimeUnit.DAYS.toMinutes(day)) - TimeUnit.HOURS.toMinutes(hours));
        StringBuilder msg = new StringBuilder();

        if (day > 0)
        {
            msg.append(day).append(" day(s), ");
        }
        if (hours > 0)
        {
            msg.append(hours).append(" hours(s), ");
        }
        if (minutes > 0)
        {
            msg.append(minutes).append(" minute(s), ");
        }

        try
        {
            msg = msg.replace(msg.lastIndexOf(","), msg.lastIndexOf(",") + 1, "");
            if (msg.toString().contains(","))
            {
                msg = msg.replace(msg.lastIndexOf(","), msg.lastIndexOf(",") + 1, " and ");
            }
        }
        catch (StringIndexOutOfBoundsException ex)
        {
            return "less than one minute...";
        }

        if (msg.toString().endsWith(" "))
        {
            return msg.toString().substring(0, msg.toString().length() - 1);
        }
        return msg.toString();
    }

    public static String dateNow()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public static String dateFormat(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat reverseFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        reverseFormat.setLenient(false);

        try
        {
            dateFormat.parse(date);
        }
        catch (ParseException ex)
        {
            try
            {
                Date newDate = reverseFormat.parse(date);
                return dateFormat.format(newDate);
            }
            catch (ParseException e)
            {
                ex.printStackTrace();
                e.printStackTrace();
            }
        }
        return date;
    }
}
