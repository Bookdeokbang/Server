package com.example.gachon.domain.notice;

import com.example.gachon.domain.history.Histories;
import com.example.gachon.domain.notice.dto.request.NoticeRequestDto;
import com.example.gachon.domain.notice.dto.response.NoticeResponseDto;
import com.example.gachon.domain.sentence.Sentences;
import com.example.gachon.domain.user.Users;
import com.example.gachon.domain.user.UsersRepository;
import com.example.gachon.global.response.code.resultCode.ErrorStatus;
import com.example.gachon.global.response.exception.handler.GeneralHandler;
import com.example.gachon.global.response.exception.handler.NoticesHandler;
import com.example.gachon.global.response.exception.handler.UsersHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticesService {

    private final NoticesRepository noticesRepository;
    private final UsersRepository usersRepository;

    NoticeResponseDto.NoticeInfoDto getNoticeInfo(Long noticeId) {
        return NoticesConverter.toNoticeInfoDto(noticesRepository.findById(noticeId).orElseThrow(() -> new NoticesHandler(ErrorStatus.NOTICE_NOT_FOUND)));
    }

    List<NoticeResponseDto.NoticePreviewDto> getNoticePreviewList() {
        List<Notices> notices = noticesRepository.findAll();

        return notices.stream()
                .map(NoticesConverter::toNoticePreviewDto)
                .collect(Collectors.toList());
    }

    public NoticeResponseDto.NoticeInfoDto getNoticeInfoByAdmin(String email, Long noticeId) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Notices notice = noticesRepository.findById(noticeId).orElseThrow(()->new NoticesHandler(ErrorStatus.NOTICE_NOT_FOUND));

            return NoticesConverter.toNoticeInfoDto(notice);
        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }

    }

    public List<NoticeResponseDto.NoticePreviewDto> getNoticePreviewListByAdmin(String email) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            List<Notices> notices = noticesRepository.findAll();

            return notices.stream()
                    .map(NoticesConverter::toNoticePreviewDto)
                    .collect(Collectors.toList());
        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void createNotice(String email, NoticeRequestDto.NoticeDto noticeDto) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Notices notice = Notices.builder()
                    .title(noticeDto.getTitle())
                    .content(noticeDto.getContent())
                    .isPinned(noticeDto.isPinned())
                    .user(reqUser)
                    .build();
            noticesRepository.save(notice);
        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void updateNotice(String email, Long noticeId, NoticeRequestDto.NoticeUpdateDto noticeUpdateDto) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Notices notice = noticesRepository.findById(noticeId).orElseThrow(()->new NoticesHandler(ErrorStatus.NOTICE_NOT_FOUND));

            notice.setTitle(noticeUpdateDto.getTitle());
            notice.setContent(noticeUpdateDto.getContent());

            noticesRepository.save(notice);
        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void updateNoticePin(String email, Long noticeId, boolean pin) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Notices notice = noticesRepository.findById(noticeId).orElseThrow(()->new NoticesHandler(ErrorStatus.NOTICE_NOT_FOUND));

            notice.setPinned(pin);

            noticesRepository.save(notice);
        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void deleteNotice(String email, Long noticeId) {
        Users reqUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (Objects.equals(reqUser.getRole(), "ADMIN")) {
            Notices notice = noticesRepository.findById(noticeId).orElseThrow(()->new NoticesHandler(ErrorStatus.NOTICE_NOT_FOUND));
            noticesRepository.delete(notice);
        } else {
            throw new GeneralHandler(ErrorStatus.UNAUTHORIZED);
        }
    }
}
