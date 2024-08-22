package com.example.demo.user.controller.response;

import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class UserCreateControllerTest {

    @Test
    void 사용자는_회원가입을_할_수_있고_회원가입된_사용자는_PENDING_상태이다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .uuidHolder(new UuidHolder() {
                    @Override
                    public String random() {
                        return "aaaaaaaa-aaaaaaa-aaaa-aaaaaaaaa-aaaaaab";
                    }
                })
                .build();
        UserCreate userCreate = UserCreate.builder()
                .email("user1@kakao.com")
                .nickname("user1")
                .address("Pangyo")
                .build();

        // when
        ResponseEntity<UserResponse> result = testContainer.userCreateController.create(userCreate);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("user1@kakao.com");
        assertThat(result.getBody().getNickname()).isEqualTo("user1");
        assertThat(result.getBody().getLastLoginAt()).isNull();
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(testContainer.userRepository.getById(1).getCertificationCode()).isEqualTo("aaaaaaaa-aaaaaaa-aaaa-aaaaaaaaa-aaaaaab");
        }
}
