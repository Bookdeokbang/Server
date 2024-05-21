package com.example.gachon.domain.sentenceInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SentencePosInfoService {

    private final SentencePosInfoRepository sentencePosInfoRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public SentencePosInfo analyzeText(String text, Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("text", text);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        Map<String, String> posTags = restTemplate.postForObject("http://34.22.93.189:8000/pos?text={text}", requestEntity, Map.class, text);

        SentencePosInfo sentencePosInfo = SentencePosInfo.builder()
                .text(text)
                .posTags(posTags)
                .sentenceId(id)
                .build();

        return sentencePosInfoRepository.save(sentencePosInfo);
    }

}
