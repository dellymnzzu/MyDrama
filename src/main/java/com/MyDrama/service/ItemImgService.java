package com.MyDrama.service;

import com.MyDrama.entity.Item;
import com.MyDrama.entity.ItemImg;
import com.MyDrama.repository.ItemImgRepository;
import com.MyDrama.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
    @Value("${itemImgLocation}")  //application.properties에 itemImgLocation 값을 주입

    private String itemImgLocation;  // 이미지가 저장될 경로를 저장하는 변수


    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{

        String oriImgName = itemImgFile.getOriginalFilename();  // 오리지널 이미지 경로
        String imgName = ""; // 저장될 이미지 파일명
        String imgUrl = ""; // 저장될 이미지의 접근 URL
        System.out.println(oriImgName);

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){ // oriImgName 문자열로 비어있지 않으면 실행
            imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());  //파일을 지정된 경로에 저장하고 저장된 파일명을 반환한다. -> sdkjg3453443jkdsfjkgjmkl.jpg
            System.out.println(imgName);
            imgUrl = "/image/item/"+imgName; // /images/item/sdkjg3453443jkdsfjkgjmkl.jpg  (이미지 URL 출력)
            System.out.println(imgUrl);
        }
        //상품 이미지 정보 저장
        // oriImgName : 상품 이미지 파일의 원래 이름
        // imgName : 실제 로컬에 저장된 상품 이미지 파일의 이름
        // imgUrl :  로컬에 저장된 상품 이미지 파일을 불러오는 경로
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        itemImgRepository.save(itemImg); // 상품 이미지 정보를 DB에 저장
    }
    //상품 이미지 업데이트 메서드
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){  // 새 이미지 파일이 업로드되었을 경우 실행한다.
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new); // 기존에 저장된 상품 이미지 정보를 조회 (없으면 예외 발생)
            // 기존에 등록된 상품 이미지 파일이 있는 경우 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getItemImgName())){
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getItemImgName());
            }
            //새로운 이미지 업로드
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());  //파일 업로드
            String imgUrl ="/image/item/"+imgName;
            //변경된 상품 이미지 정보를 세팅
            //상품 등록을 하는 경우에는 ItemImgRepository.save()로직을 호출 하지만
            //호출을 하지 않았습니다.
            //savedItemImg 엔티티는 현재 영속성 상태이다.
            // 그래서 데이터를 변경하는 것만으로 변경을 감지기능이 동작
            // 트랜잭션이 끝날때 update 쿼리가 실행 된다.
            //※ 영속성 상태여야함 사용가능 ※
            savedItemImg.updateItemImg(oriImgName,imgName,imgUrl); // 변경된 상품 이미지 정보를 업데이트


        }
    }


}
