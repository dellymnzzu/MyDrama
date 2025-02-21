package com.MyDrama.service;

import com.MyDrama.dto.BannerDto;
import com.MyDrama.entity.Banner;
import com.MyDrama.repository.BannerRepository;

import org.springframework.ui.Model;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;
    private final ContentService contentService;

    public Long saveBanner(BannerDto bannerDto, MultipartFile bannerImgFile) throws Exception {
        // 기본 배너 정보 저장
        Banner banner = bannerDto.createBanner();
        
        // 이미지 파일 처리 및 이미지 정보 업데이트
        contentService.saveBannerFile(banner, bannerImgFile);
        
        // 최종 저장
        bannerRepository.save(banner);
        
        return banner.getId();
    }

    @Transactional(readOnly = true)
    public BannerDto getBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
        .orElseThrow(EntityNotFoundException::new);
        BannerDto bannerDto = BannerDto.of(banner);
        return bannerDto;
    }

    @GetMapping("/banner/list")
    public String bannerList(Model model) {
        List<Banner> banners = bannerRepository.findAll();
        model.addAttribute("banners", banners);
        return "banner/bannerList";
    }

    // 배너 목록 조회
    @Transactional(readOnly = true)
    public List<BannerDto> getBannerList() {
        return bannerRepository.findAll().stream()
                .map(BannerDto::of)
                .collect(Collectors.toList());
    }

    //배너 상세 조회(수정을 위해)
    @Transactional(readOnly = true)
    public BannerDto getBannerDtl(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(EntityNotFoundException::new);
        return BannerDto.of(banner);  // ModelMapper가 자동으로 이미지 필드도 매핑
    }

    public void deleteBanner(Long bannerId){
        Banner banner = bannerRepository.findById(bannerId).orElseThrow(EntityNotFoundException::new);
        bannerRepository.delete(banner);
    }

    @Transactional
    public Long updateBanner(BannerDto bannerDto, MultipartFile bannerImgFile) throws Exception {
        // 배너 엔티티 조회
        Banner banner = bannerRepository.findById(bannerDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다. id = " + bannerDto.getId()));

        // 기본 정보 업데이트
        banner.updateBanner(bannerDto);

        // 이미지 파일이 있다면 이미지 정보도 업데이트
        if (!bannerImgFile.isEmpty()) {
            contentService.saveBannerFile(banner, bannerImgFile);
        }
        
        return banner.getId();
    }
}
