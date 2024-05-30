package com.example.gachon.domain.user;

import com.example.gachon.domain.user.dto.request.UserRequestDto;
import com.example.gachon.domain.user.dto.response.UserResponseDto;
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


@Tag(name = "admin-user-controller", description = "관리자 유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/users")
public class UsersAdminController {

    private final UsersService usersService;

    @GetMapping("/{userId}")
    @Operation(summary = "유저 정보 조회 API ", description = " 유저의 정보를 가져오기, UserInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<UserResponseDto.UserInfoDto> getUserInfoByAdmin(@AuthenticationPrincipal UserDetails user,
                                                                       @PathVariable Long userId) {

        return ApiResponse.onSuccess(usersService.getUserInfoByAdmin(user.getUsername(), userId));
    }

    @GetMapping("")
    @Operation(summary = "모든 유저 정보 조회 API ", description = " 모든 유저의 정보를 가져오기, List<UserInfoDto> 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<List<UserResponseDto.UserInfoDto>> getUsersInfoByAdmin(@AuthenticationPrincipal UserDetails user) {

        return ApiResponse.onSuccess(usersService.getUsersInfoByAdmin(user.getUsername()));
    }

    @GetMapping("/{userId}/notifications")
    @Operation(summary = "유저 수신 알람 조회 API ", description = " 유저가 받은 알람을 조회하기, UserNotificationListDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<UserResponseDto.UserNotificationListDto> getUserNotificationByAdmin(@AuthenticationPrincipal UserDetails user,
                                                                       @PathVariable Long userId) {

        return ApiResponse.onSuccess(usersService.getUserNotificationByAdmin(user.getUsername(), userId));
    }

    @GetMapping("/histories/{userId}")
    @Operation(summary = "유저 검색 내역 조회 API ", description = " 유저가 검색한 문장 내역 조회하기 ")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<List<Long>> getUserHistoryByAdmin(@AuthenticationPrincipal UserDetails user,
                                                                                           @PathVariable Long userId) {

        return ApiResponse.onSuccess(usersService.getUserHistoryByAdmin(user.getUsername(), userId));
    }

    @PatchMapping("/{userId}/info/update")
    @Operation(summary = "유저 정보 수정 API", description = "유저 정보 수정하기, UserUpdateDto 사용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> updateUser(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long userId,
                                                 @RequestBody UserRequestDto.UserUpdateDto userUpdateDto) {
        usersService.updateUser(user.getUsername(), userId, userUpdateDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping ("/{userId}/delete")
    @Operation(summary = "유저 삭제 요청 API ",description = "유저 삭제 하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> deleteUser(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long userId){
        usersService.deleteUser(user.getUsername(), userId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping ("/history/{userId}/{sentenceId}/delete")
    @Operation(summary = "검색 내역 삭제 API ",description = "검색 내역 삭제 하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> deleteHistoryByAdmin(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long userId, @PathVariable Long sentenceId){
        usersService.deleteHistoryByAdmin(user.getUsername(), userId, sentenceId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}