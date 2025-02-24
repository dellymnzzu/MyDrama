package com.MyDrama.service;

import com.MyDrama.dto.NoticeDto;
import com.MyDrama.entity.Notice;
import com.MyDrama.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ContentService contentService;
    private final FileService fileService;
    

    // 공지사항 삭제
    public void deleteNotice(Long noticeId) throws Exception {
        // 1. 공지사항 엔티티 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(EntityNotFoundException::new);
        
        // 3. DB에서 공지사항 데이터 삭제
        noticeRepository.delete(notice);
    }

    // 공지사항 저장
    public Long saveNotice(NoticeDto noticeDto, MultipartFile noticeImgFile) throws Exception{
       //등록
        Notice notice = noticeDto.createNotice();
        noticeRepository.save(notice);

        //이미지 파일 처리 및 이미지 정보 업데이트
        contentService.saveNoticeFile(notice,noticeImgFile);

        //최종 저장
        noticeRepository.save(notice);

        return notice.getId();
    }

    //공지사항 업데이트
    public Long updateNotice(NoticeDto noticeDto, MultipartFile noticeImgFile) throws Exception{
        //공지사항 변경
        Notice notice = noticeRepository.findById(noticeDto.getId())
                .orElseThrow(()->new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        contentService.updateNotice(noticeDto,noticeImgFile);

        return notice.getId();
    }
    
    //공지사항 상세 조회
    @Transactional(readOnly = true) // 읽기 전용
    public NoticeDto getNoticeDtl(Long noticeId){
        // 엔티티
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(EntityNotFoundException::new);
        return NoticeDto.of(notice);
    }

    //공지사항 목록 조회
    @Transactional(readOnly = true)
    public List<NoticeDto> getNoticeList(){
        return noticeRepository.findAll().stream()
                .map(NoticeDto::of)
                .collect(Collectors.toList());
    }













}
