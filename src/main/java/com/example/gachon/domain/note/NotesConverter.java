package com.example.gachon.domain.note;

import com.example.gachon.domain.note.dto.response.NoteResponseDto;

public class NotesConverter {

    public static NoteResponseDto.NoteInfoDto toNoteInfoDto(Notes note) {
        return NoteResponseDto.NoteInfoDto.builder()
                .id(note.getId())
                .userId(note.getUser().getId())
                .sentenceId(note.getSentence().getId())
                .name(note.getTitle())
                .content(note.getContent())
                .build();
    }

    public static NoteResponseDto.NotePreviewDto toNotePreviewDto(Notes note) {
        return NoteResponseDto.NotePreviewDto.builder()
                .id(note.getId())
                .name(note.getSentence().getContent())
                .build();
    }
}
