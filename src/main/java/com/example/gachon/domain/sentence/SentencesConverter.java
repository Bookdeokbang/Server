package com.example.gachon.domain.sentence;


import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
import com.example.gachon.domain.sentenceInfo.SentencePosInfo;

public class SentencesConverter {


    public static SentenceResponseDto.SentenceInfoDto toSentenceInfoDto(Sentences sentence, SentencePosInfo sentencePosInfo){
        return SentenceResponseDto.SentenceInfoDto.builder()
                .id(sentence.getId())
                .type(sentence.getType())
                .content(sentence.getContent())
                .difficulty(sentence.getDifficulty())
                .grammar(sentence.getGrammar())
                .info(toSentencePosInfoDto(sentence,sentencePosInfo))
                .build();
    }

        public static SentenceResponseDto.SentencePosInfoDto toSentencePosInfoDto(Sentences sentence, SentencePosInfo sentencePosInfo) {
            return SentenceResponseDto.SentencePosInfoDto.builder()
                    .text(sentence.getContent())
                    .posTags(sentencePosInfo.getPosTags())
                    .sentenceId(sentencePosInfo.getSentenceId())
                    .build();
        }
    }


