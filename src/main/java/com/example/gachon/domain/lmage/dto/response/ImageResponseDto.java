package com.example.gachon.domain.lmage.dto.response;

import com.example.gachon.domain.sentence.Sentences;
import com.example.gachon.domain.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageResponseDto {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageInfoDto{
        private Long id;
        private Long userId;
        private String type;
        private String name;
        private String url;

    }
}
