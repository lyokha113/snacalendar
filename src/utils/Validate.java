/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate methods
 *
 */
public class Validate implements Serializable {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_LOCATION_POINT_REGEX
            = Pattern.compile("[+-]?([0-9]*[.])?[0-9]+", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_SCRIPT_TAG_REGEX
            = Pattern.compile("<(\\\"[^\\\"]*\\\"|'[^']*'|[^'\\\">])*>", Pattern.CASE_INSENSITIVE);
    
    public static boolean validScriptTag(String content) {
        Matcher matcher = VALID_SCRIPT_TAG_REGEX.matcher(content);
        return matcher.find();
    }
    
    public static boolean validEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validLocationPoint(String point) {
        Matcher matcher = VALID_LOCATION_POINT_REGEX.matcher(point);
        return matcher.find();
    }

    public static Date validDate(String inputDate) {
        SimpleDateFormat formatter;
        if (inputDate.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            return null;
        }
        try {
            formatter.setLenient(false);
            java.util.Date date = formatter.parse(inputDate);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            return sqlDate;
        } catch (ParseException ex) {
            return null;
        }
    }
    
    
}
