package net.partala.forum.auth;

import net.partala.forum.BaseIntegrationTest;
import net.partala.forum.auth.jwt.JwtResponse;
import net.partala.forum.auth.jwt.JwtService;
import net.partala.forum.user.UserEntity;
import net.partala.forum.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseIntegrationTest {

    private final RegistrationRequest registrationRequest = new RegistrationRequest("user", "user@gmail.com", "12345678");
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void register_ShouldSaveUser() throws Exception {
        var requestJson = mapper.writeValueAsString(registrationRequest);

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn();

        var savedUser = userRepository.findByUsername(registrationRequest.username());
        assertThat(savedUser)
                .isPresent().get()
                .extracting(UserEntity::getEmail)
                .isNull();
    }

    @Test
    void login_ShouldReturnCorrectToken() throws Exception {

        authService.register(registrationRequest);
        var loginRequest = new LoginRequest(registrationRequest.username(), registrationRequest.password());
        var loginRequestJson = mapper.writeValueAsString(loginRequest);

        var resultJson = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var result = mapper.readValue(resultJson, JwtResponse.class);
        var jwtClaims = jwtService.parseAllClaims(result.token());
        assertThat(jwtClaims.getSubject()).isEqualTo(registrationRequest.username());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenLoginWithUnverifiedEmail() throws Exception {

        authService.register(registrationRequest);
        var loginRequest = new LoginRequest(registrationRequest.email(), registrationRequest.password());
        var loginRequestJson = mapper.writeValueAsString(loginRequest);

        var resultJson = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestJson))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void login_ShouldLogin_WhenLoginWithVerifiedEmail() throws Exception {

        var response = authService.register(registrationRequest);
        var savedUser = userRepository.findById(response.id());
        savedUser.get().setEmail(registrationRequest.email());
        userRepository.save(savedUser.get());
        var loginRequest = new LoginRequest(registrationRequest.email(), registrationRequest.password());
        var loginRequestJson = mapper.writeValueAsString(loginRequest);

        var resultJson = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var result = mapper.readValue(resultJson, JwtResponse.class);
        var jwtClaims = jwtService.parseAllClaims(result.token());
        assertThat(jwtClaims.getSubject()).isEqualTo(registrationRequest.username());
    }
}