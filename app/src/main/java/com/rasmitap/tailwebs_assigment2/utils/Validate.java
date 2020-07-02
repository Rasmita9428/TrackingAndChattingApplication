package com.rasmitap.tailwebs_assigment2.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by administrator on 22/1/18.
 */

public class Validate {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String NumaricPattern = "[0-9]{10}";

    public static boolean isNotNull(String txt) {
        return txt != null && txt.trim().length() > 0 ? true : false;
    }
    public static boolean isMax8(String txt) {
        return txt != null && txt.trim().length() > 5 ? true : false;
    }

    private static final String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean validate(String email) {

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatetext(String email) {

        pattern = Pattern.compile(NumaricPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
    public static boolean isValidPassword(String passwordhere) {

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");

        boolean flag=true;

        if (passwordhere.length() < 8) {

            flag=false;
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {

            flag=false;
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {

            flag=false;
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {

            flag=false;
        }

        return flag;

    }

}
