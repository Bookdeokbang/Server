package com.example.gachon.domain.sentence;


import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
import com.example.gachon.domain.sentenceInfo.SentenceInfo;

public class SentencesConverter {

    public static SentenceResponseDto.SentenceComponentInfoDto toSentenceComponentInfoDto(SentenceInfo sentenceInfo){
        return SentenceResponseDto.SentenceComponentInfoDto.builder()
                .id(sentenceInfo.getId())
                .subject(sentenceInfo.getSubject())
                .verb(sentenceInfo.getVerb())
                .object(sentenceInfo.getObject())
                .complement(sentenceInfo.getComplement())
                .adverbialPhrase(sentenceInfo.getAdverbialPhrase())
                .conjunction(sentenceInfo.getConjunction())
                .relativeClause(sentenceInfo.getRelativeClause())
                .directObject(sentenceInfo.getDirectObject())
                .indirectObject(sentenceInfo.getIndirectObject())
                .description(sentenceInfo.getDescription())
                .interpretation(sentenceInfo.getInterpretation())
                .build();
    }

    public static SentenceResponseDto.SentenceInfoDto toSentenceInfoDto(Sentences sentence, SentenceInfo sentenceInfo){
        return SentenceResponseDto.SentenceInfoDto.builder()
                .id(sentence.getId())
                .type(sentence.getType())
                .content(sentence.getContent())
                .difficulty(sentence.getDifficulty())
                .grammar(sentence.getGrammar())
                .info(toSentenceComponentInfoDto(sentenceInfo))
                .build();
    }

}
