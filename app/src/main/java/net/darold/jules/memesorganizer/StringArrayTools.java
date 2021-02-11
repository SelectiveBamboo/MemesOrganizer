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

        return (String[]) toReturn.toArray();
    }


    public String[] addStringToStrArray(String[] strArray, String strToAdd)
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.addAll(Arrays.asList(strArray));
        toReturn.add(strToAdd);

        return (String[]) toReturn.toArray();
    }


}
