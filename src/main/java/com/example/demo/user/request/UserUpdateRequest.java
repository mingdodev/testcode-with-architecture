package com.example.demo.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    private final String nickname;
    private final String address;

    @Builder
    public UserUpdateRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("address") String address) {
        this.nickname = nickname;
        this.address = address;
    }
}
