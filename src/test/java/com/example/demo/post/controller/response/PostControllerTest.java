package com.example.demo.post.controller.response;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class PostControllerTest {

    @Test
    void 사용자는_게시물을_단건_조회할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        User user = User.builder()
                .id(1L)
                .email("user1@naver.com")
                .nickname("user1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaaaaa-aaaa-aaaaaaaaa-aaaaaa")
                .lastLoginAt(100L)
                .build();
        testContainer.userRepository.save(user);
        testContainer.postRepository.save(Post.builder()
                .id(1L)
                .content("helloworld")
                .writer(user)
                .createdAt(100L)
                .build());

        // when
        ResponseEntity<PostResponse> result = testContainer.postController.getById(1);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).isEqualTo("helloworld");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("user1");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(100L);
 }

    @Test
    void 사용자는_존재하지_않는_게시물을_조회할_경우_에러가_난다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.postController.getById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
   }

    @Test
    void 사용자는_게시물을_수정할_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 200L)
                .build();
        User user = User.builder()
                .id(1L)
                .email("user1@naver.com")
                .nickname("user1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaaaaa-aaaa-aaaaaaaaa-aaaaaa")
                .lastLoginAt(100L)
                .build();
        testContainer.userRepository.save(user);
        testContainer.postRepository.save(Post.builder()
                .id(1L)
                .content("helloworld")
                .writer(user)
                .createdAt(100L)
                .build());

        // when
        ResponseEntity<PostResponse> result = testContainer.postController.update(1L, PostUpdate.builder()
                .content("gu")
                .build());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).isEqualTo("gu");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("user1");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(100L);
        assertThat(result.getBody().getModifiedAt()).isEqualTo(200L);
        }
}
