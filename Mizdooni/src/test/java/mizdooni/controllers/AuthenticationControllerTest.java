package mizdooni.controllers;

import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static mizdooni.controllers.ControllersTestUtils.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
                .hasMessage("parameters missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void login_SuccessfulLogin_ReturnsOkStatusWithLoggedInUser() {
        Map<String, String> params = createLoginParams();
        User dummyUser = mock(User.class);
        when(userService.login(USER_NAME_VALUE, USER_PASS_VALUE)).thenReturn(true);
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
        when(userService.login(USER_NAME_VALUE, USER_PASS_VALUE)).thenReturn(false);

        assertThatThrownBy(() -> controller.login(params))
                .isInstanceOf(ResponseException.class)
                .hasMessage("invalid username or password")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
