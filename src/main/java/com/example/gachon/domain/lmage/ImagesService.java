package com.example.gachon.domain.lmage;

import com.example.gachon.domain.lmage.dto.response.ImageResponseDto;
import com.example.gachon.domain.sentence.Sentences;
import com.example.gachon.domain.sentence.SentencesRepository;
import com.example.gachon.domain.user.Users;
import com.example.gachon.domain.user.UsersRepository;
import com.example.gachon.global.response.code.resultCode.ErrorStatus;
import com.example.gachon.global.response.exception.handler.GeneralHandler;
import com.example.gachon.global.response.exception.handler.ImagesHandler;
import com.example.gachon.global.response.exception.handler.UsersHandler;
import com.example.gachon.global.s3.S3Service;
import com.example.gachon.global.s3.dto.S3Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImagesService {


    private final S3Service s3Service;
    private final ImagesRepository imagesRepository;
    private final UsersRepository usersRepository;
    private final SentencesRepository sentencesRepository;

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }
    @Transactional
    public String uploadImage(MultipartFile file, String type, String email) {
        String sentence = null;
        try {
            Users user = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

//            S3Result s3Result = s3Service.uploadFile(file);

//            Images image = Images.builder()
//                    .type(type)
//                    .url(s3Result.getFileUrl())
//                    .name(file.getOriginalFilename())
//                    .user(user)
//                    .build();
//
//            imagesRepository.save(image);

            if (Objects.equals(type, "USER")) {
                try {

                    String s3_url = "https://grammary-bucket.s3.ap-northeast-2.amazonaws.com/GrammaryImage/f5f074ba-b796-4ed7-a759-e26571c223e7.PNG";

                    HttpClient httpClient = HttpClient.newHttpClient();
                    URI uri = URI.create("http://34.22.93.189:8000/ocr?image_url=" + URLEncoder.encode(s3_url, StandardCharsets.UTF_8));
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(uri)
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .header("Accept", "application/json")
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        String extractedText = response.body().replaceAll("\\\\n", " ").replaceAll("\\\\", "");

                        System.out.println(extractedText);
                        sentence = extractedText;
                    } else {
                        throw new GeneralHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (IOException | InterruptedException e) {
                    throw new GeneralHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
                }

            }
        } catch (IllegalStateException e) {
            throw new ImagesHandler(ErrorStatus.IMAGE_NOT_FOUND);
        }
        return sentence;
    }

    public List<ImageResponseDto.ImageInfoDto> getImagesByUser(Long userId) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
        List<Images> images = imagesRepository.findByUser(user);

        return images.stream()
                .map(ImagesConverter::toImageInfoDto)
                .collect(Collectors.toList());
    }
}
