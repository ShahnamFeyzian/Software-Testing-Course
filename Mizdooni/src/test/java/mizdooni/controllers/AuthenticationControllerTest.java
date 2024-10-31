package mizdooni.controllers;

import mizdooni.exceptions.DuplicatedUsernameEmail;
import mizdooni.exceptions.InvalidEmailFormat;
import mizdooni.exceptions.InvalidUsernameFormat;
import mizdooni.model.Address;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static mizdooni.controllers.ControllersTestUtils.*;
import static mizdooni.model.ModelTestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
    private UserService userService;
    private AuthenticationController controller;

    @BeforeEach
    public void setup() {
        userService = mock(UserService.class);
        controller = new AuthenticationController(userService);
    }

    @Test
    public void user_NoOneLoggedIn_ThrowsUnauthorized() {
        when(userService.getCurrentUser()).thenReturn(null);

        assertThatThrownBy(() -> controller.user())
                .isInstanceOf(ResponseException.class)
                .hasMessage("no user logged in")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void user_HasLoggedInUser_ReturnsOkStatusWithLoggedInUser() {
        User dummyUser = mock(User.class);
        when(userService.getCurrentUser()).thenReturn(dummyUser);

        Response response = controller.user();
        Object actualUser = response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualUser).isEqualTo(dummyUser);
        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void login_PassedParamsAreMissed_ThrowsBadRequest() {
        Map<String, String> params = new HashMap<>();

        assertThatThrownBy(() -> controller.login(params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_MISSING)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void login_SuccessfulLogin_ReturnsOkStatusWithLoggedInUser() {
        Map<String, String> params = createLoginParams();
        User dummyUser = mock(User.class);
        when(userService.login(DEFAULT_NAME, DEFAULT_PASS)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(dummyUser);

        Response response = controller.login(params);
        Object actualUser = response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualUser).isEqualTo(dummyUser);
        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void login_LoginFailed_ThrowsUnauthorized() {
        Map<String, String> params = createLoginParams();
        when(userService.login(DEFAULT_NAME, DEFAULT_PASS)).thenReturn(false);

        assertThatThrownBy(() -> controller.login(params))
                .isInstanceOf(ResponseException.class)
                .hasMessage("invalid username or password")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @ParameterizedTest(name = "Missed field: {0}")
    @MethodSource("signupParamsButOneOfThemDoesNotExist")
    public void signup_NecessaryParamsAreNotExist_ThrowsBadRequest(String missedField, HashMap<String, Object> params) {
        assertThatThrownBy(() -> controller.signup(params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_MISSING)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private static Stream<Arguments> signupParamsButOneOfThemDoesNotExist() {
        List<String> paramsKey = getSignupParamsKeyList();
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, Object> params = createSignupParams();
            params.remove(currentParam);
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @ParameterizedTest(name = "Bad type field: {0}")
    @MethodSource("signupParamsButOneOfThemIsNull")
    public void signup_PassedParamsDoNotHaveCorrectType_ThrowsBadRequest(String field, HashMap<String, Object> params) {
        assertThatThrownBy(() -> controller.signup(params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_BAD_TYPE)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private static Stream<Arguments> signupParamsButOneOfThemIsNull() {
        List<String> paramsKey = getSignupParamsKeyList();
        List<Arguments> args = new ArrayList<>();
        for (String currentParam : paramsKey) {
            HashMap<String, Object> params = createSignupParams();
            params.put(currentParam, 123);
            args.add(Arguments.of(currentParam, params));
        }
        return args.stream();
    }

    @ParameterizedTest(name = "Blank field: {0}")
    @MethodSource("signupParamsButOneOfTheAddressFieldIsBlank")
    public void signup_PassedAddressParamHaveBlankField_ThrowsBadRequest(String blankField, HashMap<String, Object> params) {
        assertThatThrownBy(() -> controller.signup(params))
                .isInstanceOf(ResponseException.class)
                .hasMessage(PARAMS_MISSING)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private static Stream<Arguments> signupParamsButOneOfTheAddressFieldIsBlank() {
        List<Arguments> args = new ArrayList<>();
        String[] baseKeys = {USER_NAME_KEY, USER_PASS_KEY, EMAIL_KEY};
        for (String currentParam : baseKeys) {
            HashMap<String, Object> params = createSignupParams();
            params.put(currentParam, "");
            args.add(Arguments.of(currentParam, params));
        }
        HashMap<String, Object> blankCountryAddressParams = new HashMap<>();
        HashMap<String, Object> blankCityAddressParams = new HashMap<>();
        HashMap<String, Object> blankStreetAddressParams = new HashMap<>();
        blankCountryAddressParams.put(ADDRESS_KEY, createAddressHashMap(new Address("", DEFAULT_CITY, DEFAULT_STREET)));
        blankCityAddressParams.put(ADDRESS_KEY, createAddressHashMap(new Address(DEFAULT_COUNTRY, "", DEFAULT_STREET)));
        blankStreetAddressParams.put(ADDRESS_KEY, createAddressHashMap(new Address(DEFAULT_COUNTRY, DEFAULT_CITY, "")));
        args.add(Arguments.of("address.country", blankCountryAddressParams));
        args.add(Arguments.of("address.city", blankCityAddressParams));
        args.add(Arguments.of("address.street", blankStreetAddressParams));
        return args.stream();
    }

    @Test
    public void signup_SuccessfulSignup_ReturnsOkStatusWithLoggedInUser() throws DuplicatedUsernameEmail, InvalidUsernameFormat, InvalidEmailFormat {
        HashMap<String, Object> params = createSignupParams();
        doNothing().when(userService).signup(DEFAULT_NAME, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.client);
        when(userService.login(DEFAULT_NAME, DEFAULT_PASS)).thenReturn(true);
        User dummyUser = mock(User.class);
        when(userService.getCurrentUser()).thenReturn(dummyUser);

        Response response = controller.signup(params);
        Object actualUser = response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualUser).isEqualTo(dummyUser);
        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void signup_SignupFailed_ThrowsBadRequest() throws DuplicatedUsernameEmail, InvalidUsernameFormat, InvalidEmailFormat {
        HashMap<String, Object> params = createSignupParams();
        doThrow(new InvalidEmailFormat()).when(userService)
                .signup(DEFAULT_NAME, DEFAULT_PASS, DEFAULT_EMAIL, getDefaultAddress(), User.Role.client);

        assertThatThrownBy(() -> controller.signup(params))
                .isInstanceOf(ResponseException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void logout_ThereIsLoggedInUser_ReturnsOkStatus() {
        when(userService.logout()).thenReturn(true);

        Response response = controller.logout();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void logout_ThereIsNoLoggedInUser_ThrowsUnauthorized() {
        when(userService.logout()).thenReturn(false);

        assertThatThrownBy(() -> controller.logout())
                .isInstanceOf(ResponseException.class)
                .hasMessage("no user logged in")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void validateUsername_InvalidFormat_ThrowsBadRequest() {
        assertThatThrownBy(() -> controller.validateUsername(INVALID_NAME))
                .isInstanceOf(ResponseException.class)
                .hasMessage("invalid username format")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void validateUsername_UsernameAlreadyExists_ThrowsConflict() {
        when(userService.usernameExists(DEFAULT_NAME)).thenReturn(true);

        assertThatThrownBy(() -> controller.validateUsername(DEFAULT_NAME))
                .isInstanceOf(ResponseException.class)
                .hasMessage("username already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void validateUsername_ValidUsername_ReturnsOkStatus() {
        when(userService.usernameExists(DEFAULT_NAME)).thenReturn(false);

        Response response = controller.validateUsername(DEFAULT_NAME);
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void validateEmail_InvalidFormat_ThrowsBadRequest() {
        assertThatThrownBy(() -> controller.validateEmail(INVALID_EMAIL))
                .isInstanceOf(ResponseException.class)
                .hasMessage("invalid email format")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void validateEmail_EmailAlreadyExists_ThrowsConflict() {
        when(userService.emailExists(DEFAULT_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> controller.validateEmail(DEFAULT_EMAIL))
                .isInstanceOf(ResponseException.class)
                .hasMessage("email already registered")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void validateEmail_ValidEmail_ReturnsOkStatus() {
        when(userService.emailExists(DEFAULT_EMAIL)).thenReturn(false);

        Response response = controller.validateEmail(DEFAULT_EMAIL);
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualStatus).isEqualTo(HttpStatus.OK);
    }
}
