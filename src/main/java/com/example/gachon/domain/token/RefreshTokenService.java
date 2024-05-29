package com.example.gachon.domain.token;

import com.example.gachon.domain.token.dto.request.KakaoRequestDto;
import com.example.gachon.domain.token.dto.request.LoginRequestDto;
import com.example.gachon.domain.token.dto.request.RefreshTokenRequestDto;
import com.example.gachon.domain.token.dto.request.SignUpRequestDto;
import com.example.gachon.domain.token.dto.response.SignUpResponseDto;
import com.example.gachon.domain.token.dto.response.TokenDto;
import com.example.gachon.domain.user.Status;
import com.example.gachon.domain.user.Users;
import com.example.gachon.domain.user.UsersConverter;
import com.example.gachon.domain.user.UsersRepository;
import com.example.gachon.global.jwt.JwtProvider;
import com.example.gachon.global.response.code.resultCode.ErrorStatus;
import com.example.gachon.global.response.exception.handler.GeneralHandler;
import com.example.gachon.global.response.exception.handler.UsersHandler;
import com.example.gachon.global.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String DEFAULT_PROFILE_URL = "";

    @Transactional
    public SignUpResponseDto userSignUp(SignUpRequestDto signUpRequestDto) {
        if(usersRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UsersHandler(ErrorStatus.USER_EXISTS_EMAIL);
        }

        if(usersRepository.existsByNickname(signUpRequestDto.getNickname())){
            throw new UsersHandler(ErrorStatus.USER_EXISTS_NICKNAME);
        }

        signUpRequestDto.setPassword(encodePassword(signUpRequestDto.getPassword()));

        return SignUpResponseDto.signUpResponseDto(usersRepository.save(UsersConverter.toUserSignUpDto(signUpRequestDto)));
    }

    @Transactional
    public SignUpResponseDto adminSignUp(SignUpRequestDto signUpRequestDto) {
        if(usersRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UsersHandler(ErrorStatus.USER_EXISTS_EMAIL);
        }

        if(usersRepository.existsByNickname(signUpRequestDto.getNickname())){
            throw new UsersHandler(ErrorStatus.USER_EXISTS_NICKNAME);
        }

        signUpRequestDto.setPassword(encodePassword(signUpRequestDto.getPassword()));

        return SignUpResponseDto.signUpResponseDto(usersRepository.save(UsersConverter.toAdminSignUpDto(signUpRequestDto)));
    }

    public String encodePassword(String password) {

        return bCryptPasswordEncoder.encode(password);
    }

    @Transactional
    public TokenDto userLogin(LoginRequestDto loginRequestDto) {
        if (!StringUtils.hasText(loginRequestDto.getEmail()) || !StringUtils.hasText(loginRequestDto.getPassword())) {
            throw new UsersHandler(ErrorStatus.USER_EMAIL_PASSWORD_NOT_EMPTY);
        }

        try {
            // 1. Login ID/PW를 기반으로 AuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

            // 2. authentication이 실행이 될 때, UserDetailsServiceImpl 에서 만들었던 loadUserByUsername 메서드가 실행됨
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. UserDetailsImpl에서 직접 userId 가져오기
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            Users user = usersRepository.findById(userId).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
            if (user.getStatus() == Status.DISABLED) {
                user.setStatus(Status.ENABLED);
                usersRepository.save(user);
            }

            // 4. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = jwtProvider.generateUserToken(authentication, userId.toString());
            // 5. RefreshToken 저장
            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenDto.getRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);
            return tokenDto;
        } catch (BadCredentialsException e) {
            throw new UsersHandler(ErrorStatus.USER_FAILED_TO_PASSWORD);
        } catch (AuthenticationException e) {
            throw new UsersHandler(ErrorStatus.USER_NOT_FOUND);
        }
    }

    @Transactional
    public TokenDto adminLogin(LoginRequestDto loginRequestDto) {
        if (!StringUtils.hasText(loginRequestDto.getEmail()) || !StringUtils.hasText(loginRequestDto.getPassword())) {
            throw new UsersHandler(ErrorStatus.USER_EMAIL_PASSWORD_NOT_EMPTY);
        }

        try {
            // 1. Login ID/PW를 기반으로 AuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

            // 2. authentication이 실행이 될 때, UserDetailsServiceImpl 에서 만들었던 loadUserByUsername 메서드가 실행됨
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. UserDetailsImpl에서 직접 userId 가져오기
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            // 4. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = jwtProvider.generateAdminToken(authentication, userId.toString());
            // 5. RefreshToken 저장
            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenDto.getRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);
            return tokenDto;
        } catch (BadCredentialsException e) {
            throw new UsersHandler(ErrorStatus.USER_FAILED_TO_PASSWORD);
        } catch (AuthenticationException e) {
            throw new UsersHandler(ErrorStatus.USER_NOT_FOUND);
        }
    }

    @Transactional
    public Long createUser(String id, String nickname, String email){
        Users newUser = Users.builder()
                .socialLink(id)
                .nickname(nickname)
                .name(nickname)
                .email(email)
                .status(Status.ENABLED)
                .profileUrl(DEFAULT_PROFILE_URL)
                .build();
        usersRepository.save(newUser);

        return newUser.getId();
    }

    public List<String> getKakaoUserInfo(KakaoRequestDto request) throws JsonProcessingException {
        String token = request.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String id = jsonNode.get("id").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        List<String> kakaoUserInfoList = Arrays.asList(id, nickname, email);

        return kakaoUserInfoList;
    }

    @Transactional
    public TokenDto kakaoLogin(List<String> kakaoUserInfo) {
        Long userIdd = null;

        boolean exists = usersRepository.existsBySocial(kakaoUserInfo.get(0));
        Optional<Users> user = null;
        if(!exists){
            userIdd = createUser(kakaoUserInfo.get(0), kakaoUserInfo.get(1), kakaoUserInfo.get(2));
            user = usersRepository.findById(userIdd);
        } else{
            user = usersRepository.findBySocial(kakaoUserInfo.get(0));
            userIdd = user.get().getId();
        }

        //UserDetailsImpl userDetail = new UserDetailsImpl(user.get());

        TokenDto tokenDto = jwtProvider.generateKakaoToken(userIdd.toString(), user.get().getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.get().getEmail())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    public String regenerateUserAccessToken(RefreshTokenRequestDto request){
        RefreshToken refreshToken = refreshTokenRepository.findByValue(request.getRefreshToken()).orElseThrow(()-> new GeneralHandler(ErrorStatus.INVALID_REFRESH_TOKEN));
        Users user = usersRepository.findByEmail(refreshToken.getKey()).orElseThrow(()-> new UsersHandler(ErrorStatus.JWT_NO_USER_INFO));

        return jwtProvider.regenerateUserAccessToken(user);
    }

    public String regenerateAdminAccessToken(RefreshTokenRequestDto request){
        RefreshToken refreshToken = refreshTokenRepository.findByValue(request.getRefreshToken()).orElseThrow(()-> new GeneralHandler(ErrorStatus.INVALID_REFRESH_TOKEN));
        Users user = usersRepository.findByEmail(refreshToken.getKey()).orElseThrow(()-> new UsersHandler(ErrorStatus.JWT_NO_USER_INFO));

        return jwtProvider.regenerateAdminAccessToken(user);
    }
}