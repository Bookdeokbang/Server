package com.example.gachon.domain.sentence.dto.response;

import com.example.gachon.domain.sentenceInfo.SentencePosInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class SentenceResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SentenceComponentInfoDto{
        private Long id;
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
    public static class SentenceInfoDto{
        private Long id;
        private String type;
        private String content;
        private String difficulty;
        private String grammar;
        private SentencePosInfoDto info;

    }

    @Getter
    @Builder
    public static class SentencePosInfoDto {
        private String text;
        private Long sentenceId;
        private Map<String, String> posTags;
    }

    @Getter
    @Builder
    public static class PagedSentenceInfoResponse {
            private List<SentenceInfoDto> content;
            private int pageNo;
            private int pageSize;
            private long totalElements;
            private int totalPages;
            private boolean last;
    }

}
