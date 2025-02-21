package com.MyDrama.service;

import com.MyDrama.dto.BannerDto;
import com.MyDrama.entity.Banner;
import com.MyDrama.repository.BannerRepository;
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

    private final BannerRepository bannerRepository;

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

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }
}
