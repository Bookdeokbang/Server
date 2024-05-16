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
        private String grammar;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SentenceComponentUpdateDto{
        private String subject;
        private String verb;
        private String object;
        private String complement;
        private String adverbialPhrase;
        private String conjunction;
        private String relativeClause;
        private String directObject;
        private String indirectObject;
        private String description;
        private String interpretation;

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
