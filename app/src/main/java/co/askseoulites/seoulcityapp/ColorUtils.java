package co.askseoulites.seoulcityapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hassanabid on 10/22/15.
 */
public class ColorUtils {

    /* Material design */
    public static int getMaterialBackground(int position) {
        return materialBackgroundColor().get(position);
    }
    public  static int getMaterialText(int position) {
        return materialTextColor().get(position);
    }
    public  static int getMaterialPrimary(int position) {
        return materialPrimaryColor().get(position);
    }

    public static final int getMainIconColor(int position) {
        return mainIconsColor().get(position);
    }

    /* Material colors */
    private static Map<Integer,Integer> materialBackgroundColor(){

        Map<Integer,Integer> catColorMap = new HashMap<>();

        catColorMap.put(0, R.color.theme_blue_background);
        catColorMap.put(1,R.color.theme_green_background);
        catColorMap.put(2,R.color.theme_purple_background);
        catColorMap.put(3,R.color.theme_red_background);
        catColorMap.put(4,R.color.theme_yellow_background);
        catColorMap.put(5,R.color.theme_pink_background);
        catColorMap.put(6,R.color.theme_teal_background);
        catColorMap.put(7,R.color.theme_indigo_background);

        catColorMap.put(8, R.color.theme_blue_background);
        catColorMap.put(9,R.color.theme_green_background);
        catColorMap.put(10,R.color.theme_green_background);

        return catColorMap;

    }

    private static Map<Integer,Integer> materialPrimaryColor(){

        Map<Integer,Integer> catColorMap = new HashMap<>();

        catColorMap.put(0, R.color.theme_blue_primary);
        catColorMap.put(1,R.color.theme_green_primary);

        catColorMap.put(2,R.color.theme_purple_primary);

        catColorMap.put(3,R.color.theme_red_primary);
        catColorMap.put(4,R.color.theme_yellow_primary);
        catColorMap.put(5,R.color.theme_pink_primary);
        catColorMap.put(6,R.color.theme_teal_primary);
        catColorMap.put(7,R.color.theme_indigo_primary);

        catColorMap.put(8, R.color.theme_blue_primary);
        catColorMap.put(9,R.color.theme_green_primary);

        catColorMap.put(10,R.color.theme_green_primary);

        return catColorMap;

    }

    private static Map<Integer,Integer> materialTextColor(){

        Map<Integer,Integer> catColorMap = new HashMap<>();

        catColorMap.put(0, R.color.theme_blue_text); // Koream
        catColorMap.put(1,R.color.theme_blue_text); // News

        catColorMap.put(2,R.color.theme_purple_text); // Media

        catColorMap.put(3,R.color.theme_blue_text); // Jobs
        catColorMap.put(4,R.color.theme_blue_text); // Scholarships
        catColorMap.put(5,R.color.theme_pink_text); // Tourism
        catColorMap.put(6,R.color.theme_teal_text); // History
        catColorMap.put(7,R.color.theme_indigo_text); // Social

        catColorMap.put(8, R.color.theme_blue_text); // 9
        catColorMap.put(9,R.color.theme_blue_text); // 10

        catColorMap.put(10,R.color.theme_purple_text); // 11

        return catColorMap;

    }

    private static Map<Integer,Integer> mainIconsColor(){

        Map<Integer,Integer> catColorMap = new HashMap<>();

        catColorMap.put(0, R.color.main_icon_1); // Seoul Life
        catColorMap.put(1,R.color.main_icon_2); // Study

        catColorMap.put(2,R.color.main_icon_3); // Work

        catColorMap.put(3,R.color.main_icon_4); // Travel
        catColorMap.put(4,R.color.main_icon_5); // Korean Language
        catColorMap.put(5,R.color.main_icon_6); // About us
        return catColorMap;

    }
}
