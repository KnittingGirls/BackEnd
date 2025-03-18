package com.example.knitting.girls.data.dto;

import com.example.knitting.girls.data.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;

    public UserDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
    }
}
