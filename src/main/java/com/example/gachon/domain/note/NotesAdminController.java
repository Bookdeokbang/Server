package com.example.gachon.domain.note;

import com.example.gachon.domain.note.dto.response.NoteResponseDto;
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


@Tag(name = "admin-note-controller", description = "관리자 노트 관련 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/notes")
public class NotesAdminController {

    private final NotesService notesService;

    @GetMapping("/{noteId}")
    @Operation(summary = "노트 조회 API ", description = " 노트 정보를 가져오기, NoteInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })

    public ApiResponse<NoteResponseDto.NoteInfoDto> getNoteInfoByAdmin(@AuthenticationPrincipal UserDetails user,
                                                                @PathVariable Long noteId) {

        return ApiResponse.onSuccess(notesService.getNoteInfoByAdmin(noteId, user.getUsername()));
    }
    @GetMapping("/all")
    @Operation(summary = "모든 노트 리스트 조회 API ",description = "모든 노트 정보를 리스트로 가져오기, NoteInfoDto 이용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<List<NoteResponseDto.NoteInfoDto>> getNoteInfo(@AuthenticationPrincipal UserDetails user){

        return ApiResponse.onSuccess(notesService.getNoteListByAdmin(user.getUsername()));
    }

    @DeleteMapping("/{noteId}/delete")
    @Operation(summary = "학습 노트 삭제 요청 API ",description = "학습 노트 삭제 하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })

    public ApiResponse<SuccessStatus> deleteNoteByAdmin(@AuthenticationPrincipal UserDetails user,
                                                 @PathVariable Long noteId){
        notesService.deleteNoteByAdmin(user.getUsername(), noteId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

}