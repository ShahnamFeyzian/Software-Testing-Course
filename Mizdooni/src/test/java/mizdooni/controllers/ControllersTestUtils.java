package mizdooni.controllers;

import mizdooni.model.User;

import java.util.HashMap;

import static mizdooni.model.ModelTestUtils.*;

public class ControllersTestUtils {
    public static final String USER_NAME_KEY = "username";
    public static final String USER_PASS_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String ADDRESS_KEY = "address";
    public static final String ROLE_KEY = "role";
    public static HashMap<String, String> createLoginParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put(USER_NAME_KEY, DEFAULT_NAME);
        params.put(USER_PASS_KEY, DEFAULT_PASS);
        return params;
    }
    public static HashMap<String, Object> createSignupBadParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(USER_NAME_KEY, null);
        params.put(USER_PASS_KEY, null);
        params.put(EMAIL_KEY, null);
        params.put(ADDRESS_KEY, null);
        params.put(ROLE_KEY, null);
        return params;
    }

    public static HashMap<String, Object> createSignupParams() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(USER_NAME_KEY, DEFAULT_NAME);
        params.put(USER_PASS_KEY, DEFAULT_PASS);
        params.put(EMAIL_KEY, DEFAULT_EMAIL);
        params.put(ADDRESS_KEY, getDefaultAddress());
        params.put(ROLE_KEY, User.Role.client);
        return params;
    }
}
