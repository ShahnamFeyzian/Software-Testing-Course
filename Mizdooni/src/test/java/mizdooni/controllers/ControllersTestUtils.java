package mizdooni.controllers;

import java.util.HashMap;

public class ControllersTestUtils {
    public static final String USER_NAME_KEY = "username";
    public static final String USER_NAME_VALUE = "name";
    public static final String USER_PASS_KEY = "password";
    public static final String USER_PASS_VALUE = "pass";
    public static HashMap<String, String> createLoginParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put(USER_NAME_KEY, USER_NAME_VALUE);
        params.put(USER_PASS_KEY, USER_PASS_VALUE);
        return params;
    }
}
