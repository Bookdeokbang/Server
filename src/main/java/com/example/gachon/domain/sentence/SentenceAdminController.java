package com.example.gachon.domain.sentence;

import com.example.gachon.domain.sentence.dto.request.SentenceRequestDto;
import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
import com.example.gachon.domain.word.dto.request.WordRequestDto;
import com.example.gachon.global.response.ApiResponse;
import com.example.gachon.global.response.code.resultCode.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "admin-sentences-controller", description = "관리자 문장 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/sentences")
public class SentenceAdminController {

    private final SentencesService sentencesService;

    @GetMapping("/{sentenceId}/info")
    @Operation(summary = "문장 정보 조회 API ", description = " 문장 정보를 가져오기, SentenceInfoDto, SentenceComponentInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<SentenceResponseDto.SentenceInfoDto> getSentenceInfoByAdmin(@PathVariable Long sentenceId,
                                                                                   @AuthenticationPrincipal UserDetails user) {

        return ApiResponse.onSuccess(sentencesService.getSentenceInfoByAdmin(sentenceId, user.getUsername()));
    }

    @GetMapping("")
    @Operation(summary = "조건 부 문장 정보 조회 API ", description = "조건에 맞는 모든 문장 정보를 가져오기, SentenceInfoDto, SentenceComponentInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<List<SentenceResponseDto.SentenceInfoDto>> getAllSentenceInfoWithQueryByAdmin(@AuthenticationPrincipal UserDetails user,
                                                                                                     @RequestParam String grammar,
                                                                                                     @RequestParam String difficulty) {
        return ApiResponse.onSuccess(sentencesService.getAllSentenceInfoWithQueryByAdmin(difficulty,grammar,user.getUsername()));
    }

    @PostMapping("")
    @Operation(summary = "문장 생성 API ",description = "문장 여러 개 생성하기, SentenceDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<SuccessStatus> createWord(@AuthenticationPrincipal UserDetails user,
                                                 @RequestBody SentenceRequestDto.SentenceDto sentenceDto) {
        sentencesService.createSentences(user.getUsername(), sentenceDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}