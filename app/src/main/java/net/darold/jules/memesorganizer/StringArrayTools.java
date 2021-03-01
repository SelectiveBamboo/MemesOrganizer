package net.darold.jules.memesorganizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringArrayTools {

    /**
     * Removes the first occurence of strToRemove in strArray and returns the corresponding String Array
     * @param strArray
     * @param strToremove
     *
     */
    public static String[] removeStringFromStrArray(String[] strArray, String strToremove)
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.addAll(Arrays.asList(strArray));
        toReturn.remove(strToremove);

        return  Arrays.copyOf(toReturn.toArray(), toReturn.toArray().length, String[].class);
    }


    /**
     * Adds strToAdd in strArray and returns the corresponding String Array
     * @param strArray
     * @param strToAdd
     *
     */
    public static String[] addStringToStrArray(String[] strArray, String strToAdd)
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.addAll(Arrays.asList(strArray));
        toReturn.add(strToAdd);

        return Arrays.copyOf(toReturn.toArray(), toReturn.toArray().length, String[].class);
    }

    /**
     * Converts strList to a String array and returns it
     * @param strList
     *
     */
    public static String[] StrListToStrArray(List<String> strList)
    {
        String[] strArr = new String[strList.size()];
        strArr = strList.toArray(strArr);

        return strArr;
    }
}
