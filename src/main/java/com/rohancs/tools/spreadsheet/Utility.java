package com.rohancs.tools.spreadsheet;

/**
 * Created by Rohan on 9/14/18.
 */
public class Utility {

    /***
     * Given Cell Column Number -- returns Cell Column Name
     * Example: 1 ==> A
     *          26 ==> Z
     *          27 ==> AA
     * @param columnNumber
     * @return
     */

    public static String getColumnName(int columnNumber) {
        final StringBuilder sb = new StringBuilder();

        int num = columnNumber - 1;
        while (num >=  0) {
            int numChar = (num % 26)  + 65;
            sb.append((char)numChar);
            num = (num  / 26) - 1;
        }
        return sb.reverse().toString();
    }

}
