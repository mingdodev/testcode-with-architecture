package com.example.demo.medium;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.example.demo.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
    @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private JavaMailSender mailSender;


    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "user1@naver.com";

        // when
        User result = userService.getByEmail(email);

        // then
        Assertions.assertThat(result.getNickname()).isEqualTo("user1");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        String email = "user2@naver.com";

        // when
        // then
        // 해당 람다식이 호출되면 이 에러가 발생한다.
        assertThatThrownBy(() -> {
            User result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        // when
        User result = userService.getById(1);

        // then
        Assertions.assertThat(result.getNickname()).isEqualTo("user1");
    }

    @Test
    void getById은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        // when
        // then
        // 해당 람다식이 호출되면 이 에러가 발생한다.
        assertThatThrownBy(() -> {
            User result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto를_이용하여_유저를_생성할_수_있다() {
        // given
        UserCreate userCreateDto = UserCreate.builder()
                .email("user3@naver.com")
                .address("Gyeonggi")
                .nickname("user3")
                .build();

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        User result = userService.create(userCreateDto);

        // then
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        // assertThat(result.getCertificationCode()).isEqualTo("T.T"); // FIXME
    }

    @Test
    void userUpdateDto를_이용하여_유저를_수정할_수_있다() {
        // given
        UserUpdate userUpdateDto = UserUpdate.builder()
                .address("Incheon")
                .nickname("userHI")
                .build();

        // when
        userService.update(1, userUpdateDto);

        // then
        User user = userService.getById(1);
        Assertions.assertThat(user.getId()).isNotNull();
        Assertions.assertThat(user.getAddress()).isEqualTo("Incheon");
        Assertions.assertThat(user.getNickname()).isEqualTo("userHI");
    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
        // given
        // when
        userService.login(1);

        // then
        User user = userService.getById(1);
        Assertions.assertThat(user.getLastLoginAt()).isGreaterThan(0L);
        // Assertions.assertThat(user.getLastLoginAt()).isEqualTo("너무오래걸린다이거"); // FIXME
    }

    @Test
    void PENDING_상태의_사용자는_인증코드로_ACTIVE시킬수있다() {
        // given
        // when
        userService.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

        // then
        User user = userService.getById(2);
        Assertions.assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증코드를_받으면_에러를던진다() {
        // given
        // when
        // then
       assertThatThrownBy(()->{
           userService.verifyEmail(2, "aaaadaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");
       }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
