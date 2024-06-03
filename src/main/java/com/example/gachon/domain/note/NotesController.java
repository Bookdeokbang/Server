package com.example.gachon.domain.note;

import com.example.gachon.domain.note.dto.request.NoteRequestDto;
import com.example.gachon.domain.note.dto.response.NoteResponseDto;
import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
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
@Tag(name = "note-controller", description = "사용자 노트 관련 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notes")
public class NotesController {

    private final NotesService notesService;

    @GetMapping("/{noteId}")
    @Operation(summary = "노트 조회 API ",description = " 노트 정보를 가져오기, NoteInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<NoteResponseDto.NoteInfoDto> getNoteInfo(@AuthenticationPrincipal UserDetails user,
                                                    @PathVariable Long noteId){

        return ApiResponse.onSuccess(notesService.getNoteInfo(noteId));
    }

    @PatchMapping("/{noteId}/update")
    @Operation(summary = "학습 노트 수정 요청 API ",description = "학습 노트 수정 하기, NoteDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> updateNote(@AuthenticationPrincipal UserDetails user,
                                                     @RequestBody NoteRequestDto.NoteDto noteDto,
                                                     @PathVariable Long noteId){
        notesService.updateNote(noteDto, noteId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @GetMapping("/all")
    @Operation(summary = "나의 모든 노트 리스트 조회 API ",description = "나의 모든 노트 정보를 리스트로 가져오기, NotePreviewDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<List<NoteResponseDto.NotePreviewDto>> getNoteInfo(@AuthenticationPrincipal UserDetails user){

        return ApiResponse.onSuccess(notesService.getNoteList(user.getUsername()));
    }

    @PostMapping("/memo")
    @Operation(summary = "학습 노트 메모 요청 API ",description = "학습 노트에 메모하기, NoteDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> createNoteMemo(@AuthenticationPrincipal UserDetails user,
                                                     @RequestBody NoteRequestDto.NoteDto noteDto){
        notesService.createNoteMemo(user.getUsername(), noteDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PatchMapping("/memo/{noteId}/update")
    @Operation(summary = "학습 노트 메모 수정 요청 API ",description = "학습 노트에 메모 수정 하기, NoteDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> updateNoteMemo(@AuthenticationPrincipal UserDetails user,
                                                     @RequestBody NoteRequestDto.NoteDto noteDto,
                                                     @PathVariable Long noteId){
        notesService.updateNoteMemo(noteDto, noteId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping ("/{noteId}/delete")
    @Operation(summary = "학습 노트 삭제 요청 API ",description = "학습 노트 삭제 하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> deleteNote(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long noteId){
        notesService.deleteNote(noteId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

}
