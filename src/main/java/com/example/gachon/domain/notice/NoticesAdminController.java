package com.example.gachon.domain.notice;

import com.example.gachon.domain.notice.dto.request.NoticeRequestDto;
import com.example.gachon.domain.notice.dto.response.NoticeResponseDto;
import com.example.gachon.domain.user.dto.request.UserRequestDto;
import com.example.gachon.global.response.ApiResponse;
import com.example.gachon.global.response.code.resultCode.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "admin-notice-controller", description = "관리자 공지 사항 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/notices")
public class NoticesAdminController {

    private final NoticesService noticesService;

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지 사항 정보 조회 API ", description = " 공지 사항 정보를 가져오기, NoticeInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<NoticeResponseDto.NoticeInfoDto> getNoticeInfoByAdmin(@AuthenticationPrincipal UserDetails user, @PathVariable Long noticeId) {

        return ApiResponse.onSuccess(noticesService.getNoticeInfoByAdmin(user.getUsername(), noticeId));
    }

    @GetMapping("/all")
    @Operation(summary = "모든 공지 사항 조회 API ",description = " 모든 공지사항 리스트 가져오기, NoticePreviewDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<List<NoticeResponseDto.NoticeInfoDto>> getNoticePreviewListByAdmin(@AuthenticationPrincipal UserDetails user){

        return ApiResponse.onSuccess(noticesService.getNoticePreviewListByAdmin(user.getUsername()));
    }

    @PostMapping("")
    @Operation(summary = "공지 사항 생성 API ",description = "공지사항 생성하기, NoticeDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<SuccessStatus> createNotice(@AuthenticationPrincipal UserDetails user,
                                                   @RequestBody NoticeRequestDto.NoticeDto noticeDto) {
        noticesService.createNotice(user.getUsername(), noticeDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PatchMapping("/{noticeId}/update")
    @Operation(summary = "공지 사항 정보 수정 API", description = "공지 사항 정보 수정하기, NoticeUpdateDto 사용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> updateNotice(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long noticeId,
                                                 @RequestBody NoticeRequestDto.NoticeUpdateDto noticeUpdateDto) {
        noticesService.updateNotice(user.getUsername(), noticeId, noticeUpdateDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PatchMapping("/{noticeId}/pinned")
    @Operation(summary = "공지 사항 고정 여부 수정 API", description = "공지 사항 게시판 상단에 고정 여부 수정하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> updateNoticePin(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long noticeId,
                                                 @RequestBody boolean pin) {
        noticesService.updateNoticePin(user.getUsername(), noticeId, pin);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping ("/{noticeId}/delete")
    @Operation(summary = "공지 사항 삭제 요청 API ",description = "공지 사항 삭제 하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> deleteNotice(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long noticeId){
        noticesService.deleteNotice(user.getUsername(), noticeId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
