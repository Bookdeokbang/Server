package com.example.gachon.domain.sentence;

import com.example.gachon.domain.lmage.ImagesService;
import com.example.gachon.domain.sentence.dto.request.SentenceRequestDto;
import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
import com.example.gachon.global.response.ApiResponse;
import com.example.gachon.global.response.code.resultCode.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "sentence-controller", description = "사용자 문장 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sentences")
public class SentencesController {

    private final SentencesService sentencesService;
    private final ImagesService imagesService;

    @GetMapping("/{sentenceId}/info")
    @Operation(summary = "문장 정보 조회 API ",description = " 문장 정보를 가져오기, SentenceInfoDto, SentenceComponentInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SentenceResponseDto.SentenceInfoDto> getSentenceInfo(@PathVariable Long sentenceId){

        return ApiResponse.onSuccess(sentencesService.getSentenceInfo(sentenceId));
    }

    @GetMapping("/{grammar}/{difficulty}/recommend")
    @Operation(summary = "유사한 문장 정보 조회 API ",description = " 유사한 문장 정보를 가져오기, SentenceInfoDto, SentenceComponentInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SentenceResponseDto.SentenceInfoDto> getRecommendSentence(@PathVariable String grammar,
                                                                                 @PathVariable String difficulty){

        return ApiResponse.onSuccess(sentencesService.getRecommendSentence(grammar, difficulty));
    }

    @PostMapping(path = "/input")
    @Operation(summary = "문장 직접 입력 API ",description = "문장을 직접 입력 한다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),

    })

    public ApiResponse<SuccessStatus> uploadSentence(@RequestParam String sentence
            , @AuthenticationPrincipal UserDetails user) {
        sentencesService.inputSentence(sentence, user.getUsername());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PostMapping(path = "/note")
    @Operation(summary = "문장 학습 노트 저장 API ",description = "문장을 학습 노트에 저장한다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),

    })

    public ApiResponse<SuccessStatus> sentOutNote(@RequestParam Long sentenceId
                                                  , @AuthenticationPrincipal UserDetails user) {
        sentencesService.sentOutNote(sentenceId, user.getUsername());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping ("/history/{sentenceId}/delete")
    @Operation(summary = "검색 내역 삭제 요청 API ",description = "검색 내역 삭제 하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> deleteHistory(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long sentenceId){
        sentencesService.deleteHistory(sentenceId, user.getUsername());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }




}
