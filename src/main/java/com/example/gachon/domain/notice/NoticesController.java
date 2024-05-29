package com.example.gachon.domain.notice;

import com.example.gachon.domain.notice.dto.response.NoticeResponseDto;
import com.example.gachon.domain.user.dto.response.UserResponseDto;
import com.example.gachon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Tag(name = "notice-controller", description = "사용자 공지 사항 관련 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notices")
public class NoticesController {

    private final NoticesService noticesService;
    @GetMapping("/{noticeId}")
    @Operation(summary = "공지 사항 정보 조회 API ",description = " 공지 사항 정보를 가져오기, NoticeInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<NoticeResponseDto.NoticeInfoDto> getNoticeInfo(@AuthenticationPrincipal UserDetails user, @PathVariable Long noticeId){

        return ApiResponse.onSuccess(noticesService.getNoticeInfo(noticeId));
    }

    @GetMapping("/all")
    @Operation(summary = "모든 공지 사항 조회 API ",description = " 모든 공지사항 리스트 가져오기, NoticePreviewDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<List<NoticeResponseDto.NoticeInfoDto>> getNoticePreviewList(@AuthenticationPrincipal UserDetails user){

        return ApiResponse.onSuccess(noticesService.getNoticePreviewList());
    }
}
