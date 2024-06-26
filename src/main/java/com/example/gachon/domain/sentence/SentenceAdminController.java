package com.example.gachon.domain.sentence;

import com.example.gachon.domain.inquiry.dto.request.InquiryRequestDto;
import com.example.gachon.domain.sentence.dto.request.SentenceRequestDto;
import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
import com.example.gachon.domain.word.dto.request.WordRequestDto;
import com.example.gachon.global.response.ApiResponse;
import com.example.gachon.global.response.code.resultCode.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Operation(summary = "문장 정보 조회 API ", description = " 문장 정보를 가져오기, SentenceInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<SentenceResponseDto.SentenceInfoDto> getSentenceInfoByAdmin(@PathVariable Long sentenceId,
                                                                                   @AuthenticationPrincipal UserDetails user) {

        return ApiResponse.onSuccess(sentencesService.getSentenceInfoByAdmin(sentenceId, user.getUsername()));
    }

    @GetMapping("")
    @Operation(summary = "모든 문장 정보 조회 API ", description = "모든 문장 정보를 가져오기, SentenceInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<SentenceResponseDto.PagedSentenceInfoResponse> getAllSentenceInfoWithQueryByAdmin(@AuthenticationPrincipal UserDetails user,
                                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                                     @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<SentenceResponseDto.SentenceInfoDto> sentenceInfoPage = sentencesService.getAllSentenceInfoByAdmin(user.getUsername(), pageable);
        SentenceResponseDto.PagedSentenceInfoResponse response = SentencesConverter.toPagedSentenceInfoResponse(sentenceInfoPage);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/generate")
    @Operation(summary = "문장 생성 API ",description = "문장 여러 개 생성하기, SentenceDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<SentenceResponseDto.SentenceInfoDto> createSentences(@AuthenticationPrincipal UserDetails user,
                                                 @RequestBody SentenceRequestDto.SentenceDto sentenceDto) {
        SentenceResponseDto.SentenceInfoDto sentenceInfoDto = sentencesService.createSentences(user.getUsername(), sentenceDto);
        return ApiResponse.onSuccess(sentenceInfoDto);
    }

    @PatchMapping("/{sentenceId}/update")
    @Operation(summary = "문장 수정 API", description = "문장 수정 하기, SentenceUpdateDto 사용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> updateSentence(@AuthenticationPrincipal UserDetails user,
                                                    @PathVariable Long sentenceId,
                                                    @RequestBody SentenceRequestDto.SentenceUpdateDto sentenceUpdateDto) {
        sentencesService.updateSentence(user.getUsername(), sentenceId, sentenceUpdateDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }


    @DeleteMapping ("/{sentenceId}/delete")
    @Operation(summary = "문장 삭제 API ",description = "문장 삭제 하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> deleteSentence(@AuthenticationPrincipal UserDetails user,
                                                     @PathVariable Long sentenceId){
        sentencesService.deleteSentence(user.getUsername(), sentenceId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

}