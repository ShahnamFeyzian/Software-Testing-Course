package mizdooni.controllers;

import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
    public void user_NoOneLoggedIn_ThrowsUnAuthorized() {
        when(userService.getCurrentUser()).thenReturn(null);

        assertThatThrownBy(() -> controller.user())
                .isInstanceOf(ResponseException.class)
                .hasMessage("no user logged in")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void user_HasLoggedInUser_ReturnsOkResponseWithLoggedInUser() {
        User dummyUser = mock(User.class);
        HttpStatus expectedStatus = HttpStatus.OK;
        when(userService.getCurrentUser()).thenReturn(dummyUser);

        Response response = controller.user();
        Object actualUser = response.getData();
        HttpStatus actualStatus = response.getStatus();

        assertThat(actualUser).isEqualTo(dummyUser);
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }
}
