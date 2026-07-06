package net.partala.forum.user;

import net.partala.forum.BaseIntegrationTest;
import net.partala.forum.common.AvailabilityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void isUsernameAvailable_ReturnTrue_WhenDoesntExist() throws Exception {

        String username = "user";
        userRepository.save(new UserEntity("", "user@email.com", "", UserRole.USER, AccountStatus.UNVERIFIED));

        mockMvc.perform(
            post("/users/check-availability/username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(username))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new AvailabilityResponse(true))));
    }

    @Test
    void isUsernameAvailable_ReturnFalse_WhenExists() throws Exception {

        String username = "user";
        userRepository.save(new UserEntity(username, "", "", UserRole.USER, AccountStatus.UNVERIFIED));
        userRepository.save(new UserEntity("", "user@email.com", "", UserRole.USER, AccountStatus.UNVERIFIED));

        mockMvc.perform(
                        post("/users/check-availability/username")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(username))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new AvailabilityResponse(false))));
    }

    @Test
    void isEmailAvailable_ReturnTrue_WhenDoesntExist() throws Exception {

        String email = "user@email.com";

        mockMvc.perform(
                        post("/users/check-availability/email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(email))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new AvailabilityResponse(true))));
    }

    @Test
    void isEmailAvailable_ReturnFalse_WhenExists() throws Exception {

        String email = "user@email.com";
        userRepository.save(new UserEntity("", email, "", UserRole.USER, AccountStatus.UNVERIFIED));

        mockMvc.perform(
                        post("/users/check-availability/email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(email))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new AvailabilityResponse(false))));
    }
}