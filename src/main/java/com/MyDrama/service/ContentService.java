package com.MyDrama.service;

import com.MyDrama.dto.BannerDto;
import com.MyDrama.dto.NoticeDto;
import com.MyDrama.entity.Banner;
import com.MyDrama.entity.ItemImg;
import com.MyDrama.entity.Notice;
import com.MyDrama.repository.BannerRepository;
import com.MyDrama.repository.ItemImgRepository;
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

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final BannerRepository bannerRepository;
    private final NoticeRepository noticeRepository;
    private final FileService fileService;
    private final ItemImgRepository itemImgRepository;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl ="";
        System.out.println(oriImgName);
        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){ // oriImgName 문자열로 비어 있지 않으면 실행
            System.out.println("******");
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes()); //sdkjg3453443jkdsfjkgjmkl.jpg
            System.out.println(imgName);
            imgUrl = "/itemimg/"+imgName; // /images/item/sdkjg3453443jkdsfjkgjmkl.jpg
        }
        System.out.println("1111");
        //상품 이미지 정보 저장
        // oriImgName : 상품 이미지 파일의 원래 이름
        // imgName : 실제 로컬에 저장된 상품 이미지 파일의 이름
        // imgUrl :  로컬에 저장된 상품 이미지 파일을 불러오는 경로
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        System.out.println("(((((");
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if (!itemImgFile.isEmpty()) { // 상품의 이미지를 수정한 경우 상품 이미지 업데이트
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).
                    orElseThrow(EntityNotFoundException::new); // 기존 엔티티 조회
            // 기존에 등록된 상품 이미지 파일이 있는경우 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes()); // 파일 업로드
            String imgUrl = "/itemimg/" + imgName;
            //변경된 상품 이미지 정보를 세팅
            //상품 등록을 하는 경우에는 ItemImgRepository.save()로직을 호출 하지만
            //호출을 하지 않았습니다.
            //savedItemImg 엔티티는 현재 영속성 상태이다.
            // 그래서 데이터를 변경하는 것만으로 변경을 감지기능이 동작
            // 트랜잭션이 끝날때 update 쿼리가 실행 된다.
            //※ 영속성 상태여야함 사용가능
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }



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
