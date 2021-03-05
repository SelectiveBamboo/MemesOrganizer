package net.darold.jules.memesorganizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringArrayTools {

    /**
     * Removes the first occurence of strToRemove in strArray and returns the corresponding String Array
     * @param strArray
     * @param strToremove
     */
    public static String[] removeStringFromStrArray(String[] strArray, String strToremove)
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.addAll(Arrays.asList(strArray));
        toReturn.remove(strToremove);

        return  Arrays.copyOf(toReturn.toArray(), toReturn.toArray().length, String[].class);
    }

    /**
     * Join all elements of the stringArray into  single String, with a specified delimiter between array's elements
     * Use this instead of String.join() because of the minimum API required (26 for String.join())
     * @param stringArray
     * @param delimiter
     */
    public static String joinStringArrayIntoString(String[] stringArray, String delimiter){
        if (stringArray.length < 1) {return "";}

        StringBuffer sb = new StringBuffer();

        sb.append(stringArray[0]);
        sb.append(delimiter);
        for(int i = 1; i < stringArray.length; i++) {
            sb.append(delimiter);
            sb.append(stringArray[i]);
        }
        String str = sb.toString();

        return str;
    }


    /**
     * Adds strToAdd in strArray and returns the corresponding String Array
     * @param strArray
     * @param strToAdd
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
     */
    public static String[] StrListToStrArray(List<String> strList)
    {
        String[] strArr = new String[strList.size()];
        strArr = strList.toArray(strArr);

        return strArr;
    }

    public static String[] getStrArrayContainingStr(String[] strArray, String matchingStr)
    {
        ArrayList<String> strList = new ArrayList<String>();

        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].toLowerCase().contains(matchingStr.toLowerCase()))
                strList.add(strArray[i]);
        }

        return StrListToStrArray(strList);
    }
}
