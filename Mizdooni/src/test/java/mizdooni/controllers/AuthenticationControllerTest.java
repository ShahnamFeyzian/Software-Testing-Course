package mizdooni.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.model.User;
import mizdooni.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getCurrentUser_NoOneLoggedIn_UnAuthorized() throws Exception {
        when(userService.getCurrentUser()).thenReturn(null);

        MockHttpServletResponse response = mockMvc.perform(get("/user")).andReturn().getResponse();

        User result = mapper.readValue(response.getContentAsString(), User.class);
    }
}
