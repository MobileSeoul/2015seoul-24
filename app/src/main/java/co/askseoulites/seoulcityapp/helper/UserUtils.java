package co.askseoulites.seoulcityapp.helper;

/**
 * Created by hassanabid on 10/31/15.
 */
public class UserUtils {

    public static int MAX_POINTS = 400;

    public static int getLevel(int points) {
        int level = 1;
        if(points > 0 && points <= 50)
            level = 1;
        else if(points > 50 && points < 100)
            level = 2;
        else if(points > 100 && points <= 150)
            level = 3;
        else if(points > 150 && points <= 200)
            level = 4;
        else if(points > 200 && points <= 250)
            level = 5;
        else if(points > 250 && points <= 300)
            level = 6;
        else if(points > 300 && points <= 350)
            level = 7;
        else if(points > 350 && points <= 1000)
            level = 8;

         return level;
    }


}
