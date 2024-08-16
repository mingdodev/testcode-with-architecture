package com.example.demo.user.controller;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserJpaRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소거된채_전달받을_수_있다() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user1@naver.com"))
                .andExpect(jsonPath("$.nickname").value("user1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.address").doesNotExist());
    }

    @Test
    void 사용자는_존재하지_않는_유저의_아이디로_api_호출할경우_404_응답을_받는다() throws Exception {
        mockMvc.perform(get("/api/users/3443431"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 3443431를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는_인증코드로_계정을_활성화할_수_있다() throws Exception {
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
                .andExpect(status().isFound());
        UserEntity userEntity = userRepository.findById(1L).get();
        Assertions.assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증코드가_일치하지_않을_경우_권한_없음_에러를_내려준다() throws Exception {
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaac"))
                .andExpect(status().isForbidden());
    }

    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고올_수_있다() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL", "user1@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user1@naver.com"))
                .andExpect(jsonPath("$.nickname").value("user1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.address").value("Seoul"));
    }

    @Test
    void 사용자는_내_정보를_수정할_수_있다() throws Exception {
        // given
        UserUpdate userUpdateDto = UserUpdate.builder()
                .nickname("user123")
                .address("Pangyo")
                .build();

        // when
        // then
        mockMvc.perform(
                put("/api/users/me")
                        .header("EMAIL", "user1@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user1@naver.com"))
                .andExpect(jsonPath("$.nickname").value("user123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.address").value("Pangyo"));
    }
}
