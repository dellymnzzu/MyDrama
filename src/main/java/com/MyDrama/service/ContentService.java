package com.MyDrama.service;

import com.MyDrama.dto.BannerDto;
import com.MyDrama.dto.NoticeDto;
import com.MyDrama.entity.Banner;
import com.MyDrama.entity.Notice;
import com.MyDrama.repository.BannerRepository;
import com.MyDrama.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.io.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ContentService {
    @Value("${bannerLocation}")
    private String bannerLocation;

    @Value("${noticeLocation}")
    private String noticeLocation;

    private final BannerRepository bannerRepository;
    private final NoticeRepository noticeRepository;
    private final FileService fileService;


    public void saveBannerFile(Banner banner, MultipartFile bannerImgFile) throws Exception {
        String oriImgName = bannerImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if(!StringUtils.isEmpty(oriImgName)) {
            imgName = UUID.randomUUID().toString() + "_" + oriImgName;
            String uploadPath = bannerLocation + "/" + imgName;
            FileUtils.writeByteArrayToFile(new File(uploadPath), bannerImgFile.getBytes());
            imgUrl = "/banner/" + imgName;
        }

        // 엔티티의 업데이트 메서드 사용
        banner.updateBannerImage(oriImgName, imgName, imgUrl);
    }

    // 공지사항 파일 저장
    public void saveNoticeFile(Notice notice, MultipartFile noticeImgFile) throws Exception{
        String oriImgName = noticeImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl ="";

        if(!StringUtils.isEmpty(oriImgName)){
            imgName = UUID.randomUUID().toString()+"_"+oriImgName;
            String uploadPath = noticeLocation+"/"+imgName;
            FileUtils.writeByteArrayToFile(new File(uploadPath),noticeImgFile.getBytes());
            imgUrl = "/notice/"+imgName;
        }
         notice.updateNoticeImg(notice.getTitle(),notice.getDescription(),oriImgName,imgName,imgUrl);
    }

    public void updateNotice(NoticeDto noticeDto, MultipartFile noticeImgFile) throws Exception {
        Notice saveNoticeFile = noticeRepository.findById(noticeDto.getId())
                .orElseThrow(EntityNotFoundException::new);
                
        if(!noticeImgFile.isEmpty()) {
            if(!StringUtils.isEmpty(saveNoticeFile.getImgName())) {
                // 기존 이미지 삭제
                fileService.deleteFile(noticeLocation + "/" + saveNoticeFile.getImgName());
            }
            
            String oriImgName = noticeImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(noticeLocation, oriImgName, noticeImgFile.getBytes());
            String imgUrl = "/notice/" + imgName;  // imgUrl 수정
            
            saveNoticeFile.updateNoticeImg(noticeDto.getTitle(), noticeDto.getDescription(), 
                                         oriImgName, imgName, imgUrl);
        } else {
            saveNoticeFile.updateNotice(noticeDto.getTitle(), noticeDto.getDescription());
        }
    }



}
