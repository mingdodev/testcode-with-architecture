package com.example.demo.post.service;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.infrastructure.PostEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PostServiceTest {
    @Autowired
    private PostService postService;

    @Test
    void getById는_존재하는_게시물을_내려준다() {
        // given
        // when
        Post result = postService.getById(1);

        // then
        Assertions.assertThat(result.getContent()).isEqualTo("helloworld");
        Assertions.assertThat(result.getWriter().getEmail()).isEqualTo("user1@naver.com");
    }

    @Test
    void postCreateDto로_게시글_작성() {
        // given
        PostCreate postCreateDto = PostCreate.builder()
                .writerId(1)
                .content("foobar")
                .build();

        // when
        Post result = postService.create(postCreateDto);

        // then
        System.out.println(result.getModifiedAt());
        Assertions.assertThat(result.getContent()).isEqualTo("foobar");
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getCreatedAt()).isGreaterThan(0);
    }

    @Test
    void postUpdateDto로_게시글_수정() {
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("그러지마세요")
                .build();

        // when
        postService.update(1, postUpdate);

        // then
        Post post = postService.getById(1);
        Assertions.assertThat(post.getContent()).isEqualTo("그러지마세요");
        Assertions.assertThat(post.getModifiedAt()).isGreaterThan(0);
    }

}
