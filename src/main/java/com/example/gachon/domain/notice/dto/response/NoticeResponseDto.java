package com.example.gachon.domain.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NoticeResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeInfoDto{
        private Long noticeId;
        private String title;
        private String content;
        private boolean isPinned;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticePreviewDto{
        private Long noticeId;
        private String title;
        private boolean isPinned;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;

    }
}
