package com.example.demo.post.domain;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class PostTest {

    @Test
    public void PostCreate로_글을_작성할_수_있다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("helloworld")
                .build();

        User writer = User.builder()
                .id(1L)
                .email("user1@naver.com")
                .nickname("user1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();

        // when
        Post post = Post.from(writer, postCreate, new TestClockHolder(1678530673958L));

        // then
        Assertions.assertThat(post.getContent()).isEqualTo("helloworld");
        Assertions.assertThat(post.getWriter().getEmail()).isEqualTo("user1@naver.com");
        Assertions.assertThat(post.getCreatedAt()).isEqualTo(1678530673958L);
        Assertions.assertThat(post.getWriter().getNickname()).isEqualTo("user1");
        Assertions.assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        Assertions.assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        Assertions.assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");
    }

    @Test
    public void PostUpdate로_게시물을_수정할_수_있다() {
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("안 helloworld")
                .build();

        User writer = User.builder()
                .id(1L)
                .email("user1@naver.com")
                .nickname("user1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();

        Post post = Post.builder()
                .id(1L)
                .content("helloworld")
                .createdAt(1678530673958L)
                .modifiedAt(0L)
                .writer(writer)
                .build();

        // when
        post = post.update(postUpdate, new TestClockHolder(1678530673958L));

        // then
        Assertions.assertThat(post.getContent()).isEqualTo("안 helloworld");
        Assertions.assertThat(post.getWriter().getEmail()).isEqualTo("user1@naver.com");
        Assertions.assertThat(post.getCreatedAt()).isEqualTo(1678530673958L);
        Assertions.assertThat(post.getWriter().getNickname()).isEqualTo("user1");
        Assertions.assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        Assertions.assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        Assertions.assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");
    }
}
