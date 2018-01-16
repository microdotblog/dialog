package com.dialogapp.dialog;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.VerifiedAccount;

public class TestUtil {

    public static AccountResponse createAccount(String token) {
        return new AccountResponse();
    }

    public static VerifiedAccount createUnverifiedAccount(boolean invalidAccount) {
        if (invalidAccount)
            return new VerifiedAccount(null, null, null, null,
                    null, null, "api token is invalid");
        else
            return new VerifiedAccount("foo", "foobar", "foo.bar",
                    true, true, "foo.bar", null);
    }
}
