package com.example.gachon.domain.sentenceInfo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SentencePosInfoRepository extends MongoRepository<SentencePosInfo, String> {
    Optional<SentencePosInfo> findBySentenceId(Long sentenceId);
}
