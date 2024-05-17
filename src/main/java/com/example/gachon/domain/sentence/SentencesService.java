package com.example.gachon.domain.sentence;

import com.example.gachon.domain.history.Histories;
import com.example.gachon.domain.history.HistoriesRepository;
import com.example.gachon.domain.note.Notes;
import com.example.gachon.domain.note.NotesRepository;
import com.example.gachon.domain.sentence.dto.request.SentenceRequestDto;
import com.example.gachon.domain.sentence.dto.response.SentenceResponseDto;
import com.example.gachon.domain.sentenceInfo.SentenceInfo;
import com.example.gachon.domain.sentenceInfo.SentenceInfoRepository;
import com.example.gachon.domain.user.Users;
import com.example.gachon.domain.user.UsersRepository;
import com.example.gachon.global.response.code.resultCode.ErrorStatus;
import com.example.gachon.global.response.exception.handler.GeneralHandler;
import com.example.gachon.global.response.exception.handler.HistoriesHandler;
import com.example.gachon.global.response.exception.handler.SentencesHandler;
import com.example.gachon.global.response.exception.handler.UsersHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SentencesService {

    private final SentencesRepository sentencesRepository;
    private final SentenceInfoRepository sentenceInfoRepository;
    private final UsersRepository usersRepository;
    private final HistoriesRepository historiesRepository;
    private final NotesRepository notesRepository;



    SentenceResponseDto.SentenceInfoDto getSentenceInfo(Long sentenceId) {
        SentenceInfo sentenceInfo = sentenceInfoRepository.findBySentenceId(sentenceId).orElseThrow(() -> new SentencesHandler(ErrorStatus.SENTENCE_INFO_NOT_FOUND));
        Sentences sentence = sentencesRepository.findById(sentenceId).orElseThrow(()->new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        return SentencesConverter.toSentenceInfoDto(sentence, sentenceInfo);
    }

    SentenceResponseDto.SentenceInfoDto getRecommendSentence(String grammar, String difficulty) {
        List<Sentences> sentences = sentencesRepository.findAllByGrammarAndDifficulty(grammar, difficulty);
        if (sentences.isEmpty()) {
            throw new SentencesHandler(ErrorStatus.SENTENCE_NOT_FOUND);
        }
        int randomIndex = (int)(Math.random() * sentences.size());
        Sentences sentence = sentences.get(randomIndex);
        SentenceInfo sentenceInfo = sentenceInfoRepository.findBySentenceId(sentence.getId()).orElseThrow(() -> new SentencesHandler(ErrorStatus.SENTENCE_INFO_NOT_FOUND));

        return SentencesConverter.toSentenceInfoDto(sentence, sentenceInfo);
    }

    public String predictSentence(String sentence) {

        String apiUrl = "http://34.22.93.189:8000/predict?model=roberta";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        List<String> requestBody = Arrays.asList(sentence);

        HttpEntity<List<String>> requestEntity = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject(apiUrl, requestEntity, String.class);
        int labelIndex = response.indexOf("\"label\":\"") + "\"label\":\"".length();
        String extractedLabel = response.substring(labelIndex, response.indexOf("\"", labelIndex));

        return extractedLabel;
    }

    @Transactional
    public void inputSentence(String sentence, String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(()-> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        String grammar = predictSentence(sentence);

        String difficulty;

        if (sentence.length() <= 10) {
            difficulty = "LOW";
        } else if (sentence.length() <= 20) {
            difficulty = "MIDDLE";
        } else {
            difficulty = "HIGH";
        }
        Sentences sentenceObject = Sentences.builder()
                .content(sentence)
                .type("USER")
                .user(user)
                .grammar(grammar)
                .difficulty(difficulty)
                .build();

        sentencesRepository.save(sentenceObject);

      Histories histories = Histories.builder()
                .user(user)
                .sentence(sentenceObject)
                .timestamp(LocalDateTime.now())
                .build();

        historiesRepository.save(histories);

    }

    @Transactional
    public void sentOutNote(Long sentenceId, String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(()-> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
        Sentences sentence = sentencesRepository.findById(sentenceId).orElseThrow(()-> new SentencesHandler(ErrorStatus.SENTENCE_NOT_FOUND));

        Notes note = Notes.builder()
                .sentence(sentence)
                .user(user)
                .build();

        notesRepository.save(note);
    }

    @Transactional
    public void deleteHistory(Long sentenceId, String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(()-> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
        Sentences sentence = sentencesRepository.findById(sentenceId).orElseThrow(()-> new SentencesHandler(ErrorStatus.SENTENCE_NOT_FOUND));
        Histories history = historiesRepository.findByUserAndSentence(user, sentence).orElseThrow(()-> new HistoriesHandler(ErrorStatus.HISTORY_NOT_FOUND));

        historiesRepository.delete(history);
    }

    public SentenceResponseDto.SentenceInfoDto getSentenceInfoByAdmin(Long sentenceId, String email) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            SentenceInfo sentenceInfo = sentenceInfoRepository.findBySentenceId(sentenceId).orElseThrow(() -> new SentencesHandler(ErrorStatus.SENTENCE_INFO_NOT_FOUND));
            Sentences sentence = sentencesRepository.findById(sentenceId).orElseThrow(()->new UsersHandler(ErrorStatus.USER_NOT_FOUND));

            return SentencesConverter.toSentenceInfoDto(sentence, sentenceInfo);

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }

    }

    public List<SentenceResponseDto.SentenceInfoDto> getAllSentenceInfoWithQueryByAdmin(String difficulty, String grammar, String email) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            List<Sentences> sentences;
            List<SentenceInfo> sentenceInfos;

            if (!difficulty.isEmpty()) {
                if (!grammar.isEmpty()) {
                    sentences = sentencesRepository.findAllByGrammarAndDifficulty(grammar,difficulty);
                } else {
                    sentences = sentencesRepository.findAllByDifficulty(difficulty);
                }
            } else {
                sentences = sentencesRepository.findAllByGrammar(grammar);
            }

            List<Long> sentenceIdList = sentences.stream()
                    .map(Sentences::getId)
                    .toList();

            sentenceInfos = sentenceIdList.stream()
                    .map(id -> sentenceInfoRepository.findBySentenceId(id).orElseThrow(()-> new SentencesHandler(ErrorStatus.SENTENCE_INFO_NOT_FOUND)))
                    .toList();

            List<SentenceResponseDto.SentenceInfoDto> sentenceInfoDtoList = new ArrayList<>();

            for (int i = 0; i < sentences.size(); i++ ) {
                sentenceInfoDtoList.add(SentencesConverter.toSentenceInfoDto(sentences.get(i),sentenceInfos.get(i)));
            }
            return sentenceInfoDtoList;


        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }

    }

    @Transactional
    public void createSentences(String email, SentenceRequestDto.SentenceDto sentenceDto) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            try {
                HttpClient httpClient = HttpClient.newHttpClient();
                URI uri = URI.create("http://34.22.93.189:8000/generate?label=" + URLEncoder.encode(sentenceDto.getGrammar(), StandardCharsets.UTF_8));
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    String generatedSentence = response.body();
                    int colonIndex = generatedSentence.indexOf(":");
                    String grammar = generatedSentence.substring(0, colonIndex).trim().replaceAll("\"", "");
                    String content = generatedSentence.substring(colonIndex + 2, generatedSentence.length() - 1).trim();
                    int contentLength = content.length();
                    String difficulty;

                    if (contentLength <= 10) {
                        difficulty = "LOW";
                    } else if (contentLength <= 20) {
                        difficulty = "MIDDLE";
                    } else {
                        difficulty = "HIGH";
                    }

                    Sentences sentence = Sentences.builder()
                            .user(reqUser)
                            .type("AI_GENERATED")
                            .content(content)
                            .difficulty(difficulty)
                            .grammar(grammar)
                            .build();

                    sentencesRepository.save(sentence);
                } else {
                    throw new GeneralHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (IOException | InterruptedException e) {
                throw new GeneralHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void updateSentence(String email, Long sentenceId, SentenceRequestDto.SentenceUpdateDto sentenceUpdateDto) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Sentences sentence = sentencesRepository.findById(sentenceId).orElseThrow(()-> new SentencesHandler(ErrorStatus.SENTENCE_NOT_FOUND));

            sentence.setType(sentenceUpdateDto.getType());
            sentence.setContent(sentence.getContent());
            sentence.setDifficulty(sentence.getDifficulty());
            sentence.setGrammar(sentence.getGrammar());

            sentencesRepository.save(sentence);

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void updateSentenceComponent(String email, Long sentenceId, SentenceRequestDto.SentenceComponentUpdateDto sentenceComponentUpdateDto) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            SentenceInfo sentenceInfo = sentenceInfoRepository.findBySentenceId(sentenceId).orElseThrow(() -> new SentencesHandler(ErrorStatus.SENTENCE_INFO_NOT_FOUND));

            sentenceInfo.setSubject(sentenceComponentUpdateDto.getSubject());
            sentenceInfo.setVerb(sentenceComponentUpdateDto.getVerb());
            sentenceInfo.setObject(sentenceComponentUpdateDto.getObject());
            sentenceInfo.setComplement(sentenceComponentUpdateDto.getComplement());
            sentenceInfo.setAdverbialPhrase(sentenceComponentUpdateDto.getAdverbialPhrase());
            sentenceInfo.setConjunction(sentenceComponentUpdateDto.getConjunction());
            sentenceInfo.setRelativeClause(sentenceComponentUpdateDto.getRelativeClause());
            sentenceInfo.setDirectObject(sentenceComponentUpdateDto.getDirectObject());
            sentenceInfo.setIndirectObject(sentenceComponentUpdateDto.getIndirectObject());
            sentenceInfo.setDescription(sentenceComponentUpdateDto.getDescription());
            sentenceInfo.setInterpretation(sentenceComponentUpdateDto.getInterpretation());

            sentenceInfoRepository.save(sentenceInfo);

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void deleteSentence(String email, Long sentenceId) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Sentences sentence = sentencesRepository.findById(sentenceId).orElseThrow(()-> new SentencesHandler(ErrorStatus.SENTENCE_NOT_FOUND));
            sentencesRepository.delete(sentence);

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }
}
