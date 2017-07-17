package io.github.asherslab.luckrankup.utils;

import io.github.asherslab.luckrankup.Luckrankup;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommonUtils
{
    private static Luckrankup plugin;

    public static String DateNow()
    {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date today = Calendar.getInstance().getTime();
        String now = df.format(today);
        return now;
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

    public static org.spongepowered.api.text.Text toText(String str)
    {
        return TextSerializers.FORMATTING_CODE.deserialize(str);
    }
}
