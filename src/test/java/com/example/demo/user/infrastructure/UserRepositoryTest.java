package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest(showSql = true)
@Sql("/sql/user-repository-test-data.sql")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

//    @Test
//    void UserRepository_가_제대로_연결되었다() {
//        // given
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail("alstj3224@naver.com");
//        userEntity.setAddress("Seoul");
//        userEntity.setNickname("hi");
//        userEntity.setStatus(UserStatus.ACTIVE);
//        userEntity.setCertificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
//
//        // when
//        UserEntity result = userRepository.save(userEntity);
//
//        // then
//        Assertions.assertThat(result.getId()).isNotNull();
//    }

    @Test
    void findByIdAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given

        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.ACTIVE);

        // then
        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus_는_데이터가_없으면_Optional_empty를_내려준다 () {
        // given

        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.PENDING);

        // then
        Assertions.assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findByEmailAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given

        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("alstj3224@naver.com", UserStatus.ACTIVE);

        // then
        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByEmailAndStatus_는_데이터가_없으면_Optional_empty를_내려준다 () {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("alstj3224@naver.com", UserStatus.PENDING);

        // then
        Assertions.assertThat(result.isEmpty()).isTrue();
    }
}
