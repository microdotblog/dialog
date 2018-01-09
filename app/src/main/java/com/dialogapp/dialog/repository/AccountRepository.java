package com.dialogapp.dialog.repository;

public class AccountRepository {

    public static boolean isValid(String token) {
        if (token.length() < 20)
            return false;
        else
            return true;
    }
}
