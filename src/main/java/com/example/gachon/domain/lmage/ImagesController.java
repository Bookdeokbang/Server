package com.example.gachon.domain.lmage;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "image-controller", description = "이미지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/image")
public class ImagesController {

    private final ImagesService imagesService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문장 이미지 업로드 API ",description = "문장 이미지를 업로드한다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "IMAGE402", description = "이미지 업로드 실패",content = @Content(schema = @Schema(implementation = ApiResponse.class))),

    })

    public ApiResponse<SuccessStatus> uploadImage(@RequestParam MultipartFile file
            , @AuthenticationPrincipal UserDetails user, @RequestParam String type) {
        imagesService.uploadImage(file, type, user.getUsername());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
