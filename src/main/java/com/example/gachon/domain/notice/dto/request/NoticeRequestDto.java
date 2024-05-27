package com.example.gachon.domain.notice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class NoticeRequestDto {

    @Getter
    @Builder
    public static class NoticeDto {
        private String title;
        private String content;
        private boolean isPinned;
    }

    @Getter
    public static class NoticeUpdateDto {
        private String title;
        private String content;
    }


}
