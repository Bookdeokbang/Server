package com.example.gachon.domain.sentenceInfo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection= "pos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentencePosInfo {

    @Id
    private String id;
    private String text;
    private Long sentenceId;
    private Map<String, String> posTags;
}
