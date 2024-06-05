package com.example.gachon.domain.sentence.dto.request;

import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
public class SentenceRequestDto {

    @Getter
    @Builder
    public static class SentenceDto {
        private Long count;
        private String grammar;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SentenceUpdateDto{
        private String type;
        private String content;
        private String difficulty;
        private String grammar;

    }
}
