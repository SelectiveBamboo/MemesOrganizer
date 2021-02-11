package net.darold.jules.memesorganizer;

import java.util.ArrayList;
import java.util.Arrays;

public class StringArrayTools {

    /**
     * Remove the first occurence of strToRemove in strArray and return the corresponding String Array
     * @param strArray
     * @param strToremove
     * @return
     */
    public String[] removeStringFromStrArray(String[] strArray, String strToremove)
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.addAll(Arrays.asList(strArray));
        toReturn.remove(strToremove);

        return  Arrays.copyOf(toReturn.toArray(), toReturn.toArray().length, String[].class);
    }


    /**
     * Add strToAdd in strArray and return the corresponding String Array
     * @param strArray
     * @param strToAdd
     * @return
     */
    public String[] addStringToStrArray(String[] strArray, String strToAdd)
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.addAll(Arrays.asList(strArray));
        toReturn.add(strToAdd);

        return Arrays.copyOf(toReturn.toArray(), toReturn.toArray().length, String[].class);
    }


}
