package com.example.gachon.domain.token;

import com.example.gachon.domain.token.dto.request.KakaoRequestDto;
import com.example.gachon.domain.token.dto.request.LoginRequestDto;
import com.example.gachon.domain.token.dto.request.RefreshTokenRequestDto;
import com.example.gachon.domain.token.dto.request.SignUpRequestDto;
import com.example.gachon.domain.token.dto.response.RegenerateTokenResponseDto;
import com.example.gachon.domain.token.dto.response.TokenDto;
import com.example.gachon.global.response.ApiResponse;
import com.example.gachon.global.response.code.resultCode.SuccessStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "user-token-controller", description = "일반 유저 로컬 로그인, 회원가입 API")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class RefreshTokenUserController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/local/sign-up")
    @Operation(summary = "로컬 회원가입", description = "JWT 이용해 어드민 로컬 회원가입 진행, SignUpRequestDto, SignUpResponseDto 사용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER401", description = "이미 존재하는 이메일입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER411", description = "이미 존재하는 닉네임입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<SuccessStatus> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        refreshTokenService.userSignUp(signUpRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PostMapping("/local/login")
    @Operation(summary = "로컬 로그인", description = "JWT 이용해 로컬 로그인 진행")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER403", description = "비밀번호가 잘못되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER415", description = "이메일 또는 비밀번호를 입력하지 않았습니다."),
    })
    public ApiResponse<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ApiResponse.onSuccess(refreshTokenService.userLogin(loginRequestDto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 만료시 AccessToken 재발급", description = "RefreshToken을 받아 AccessToken 재발급, RefreshTokenRequestDto & RegenerateTokenResponseDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH407", description = "리프레시 토큰이 유효하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH405", description = "토큰 속 유저 정보가 유효하지 않습니다."),
    })
    public ApiResponse<RegenerateTokenResponseDto> refresh(@RequestBody RefreshTokenRequestDto request){
        String accessToken = refreshTokenService.regenerateUserAccessToken(request);
        return ApiResponse.onSuccess(RefreshTokenConverter.toRegenerateTokenDto(accessToken));
    }

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인", description = "카카오유저 정보가 담긴 token을 KakaoRequestDto로 받아 유저 확인 후 TokenDto반환")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<TokenDto> kakaoLogin(@RequestBody KakaoRequestDto request) throws JsonProcessingException {
        List<String> kakaoUserInfo = refreshTokenService.getKakaoUserInfo(request);
        return ApiResponse.onSuccess(refreshTokenService.kakaoLogin(kakaoUserInfo));
    }


}