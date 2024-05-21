package com.example.gachon.domain.sentenceInfo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SentencePosInfoRepository extends MongoRepository<SentencePosInfo, String> {
    Optional<SentencePosInfo> findBySentenceId(Long sentenceId);
}
