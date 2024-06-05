package com.example.gachon.domain.word;

import com.example.gachon.domain.sentence.Sentences;
import com.example.gachon.domain.sentence.SentencesConverter;
import com.example.gachon.domain.user.Users;
import com.example.gachon.domain.user.UsersRepository;
import com.example.gachon.domain.word.dto.request.WordRequestDto;
import com.example.gachon.domain.word.dto.response.WordResponseDto;
import com.example.gachon.global.response.code.resultCode.ErrorStatus;
import com.example.gachon.global.response.exception.handler.GeneralHandler;
import com.example.gachon.global.response.exception.handler.SentencesHandler;
import com.example.gachon.global.response.exception.handler.UsersHandler;
import com.example.gachon.global.response.exception.handler.WordsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordsService {

    private final WordsRepository wordsRepository;
    private final UsersRepository usersRepository;

    public List<WordResponseDto.WordInfoDto> getWordList() {
        List<Words> words = wordsRepository.findAll();
        return words.stream()
                .map(WordsConverter::toWordInfoDto)
                .toList();
    }

    public WordResponseDto.WordInfoDto getWordInfoByAdmin(String email, Long wordId) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Words word = wordsRepository.findById(wordId).orElseThrow(()->new WordsHandler(ErrorStatus.WORD_NOT_FOUND));

            return WordsConverter.toWordInfoDto(word);

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    public List<WordResponseDto.WordInfoDto> getWordListByAdmin(String email) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            List<Words> words = wordsRepository.findAll();
            return words.stream()
                    .map(WordsConverter::toWordInfoDto)
                    .toList();

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void createWord(String email, List<WordRequestDto.WordDto> wordListDto) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            for (WordRequestDto.WordDto wordDto : wordListDto) {
                Words word = Words.builder()
                        .name(wordDto.getName())
                        .meaning(wordDto.getMeaning())
                        .build();

                wordsRepository.save(word);
            }

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void updateWord(String email, Long wordId, WordRequestDto.WordDto wordDto) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Words word = wordsRepository.findById(wordId).orElseThrow(()->new WordsHandler(ErrorStatus.WORD_NOT_FOUND));

            word.setName(wordDto.getName());
            word.setMeaning(word.getMeaning());

            wordsRepository.save(word);

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void deleteWord(String email, Long wordId) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Words word = wordsRepository.findById(wordId).orElseThrow(()->new WordsHandler(ErrorStatus.WORD_NOT_FOUND));
            wordsRepository.delete(word);

        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }
}
