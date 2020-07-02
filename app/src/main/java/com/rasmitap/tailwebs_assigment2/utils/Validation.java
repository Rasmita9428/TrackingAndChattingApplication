package com.rasmitap.tailwebs_assigment2.utils;

public class Validation {
    public static boolean isRequiredField(String strText) {
        return strText != null && !strText.trim().isEmpty();
    }

}
