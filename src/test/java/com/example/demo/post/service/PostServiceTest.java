package com.example.demo.post.service;

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
        PostEntity result = postService.getById(1);

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
        PostEntity result = postService.create(postCreateDto);

        // then
        Assertions.assertThat(result.getContent()).isEqualTo("foobar");
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getCreatedAt()).isGreaterThan(0);
    }

    @Test
    void postUpdateDto로_게시글_수정() {
        // given
        PostUpdate postUpdateDto = PostUpdate.builder()
                .content("--.-.--.")
                .build();

        // when
        postService.update(1, postUpdateDto);

        // then
        PostEntity postEntity = postService.getById(1);
        Assertions.assertThat(postEntity.getContent()).isEqualTo("--.-.--.");
        Assertions.assertThat(postEntity.getModifiedAt()).isGreaterThan(0);
    }

}
