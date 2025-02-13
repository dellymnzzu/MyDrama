package com.MyDrama.service;


import com.MyDrama.dto.ItemFormDto;
import com.MyDrama.dto.ItemImgDto;
import com.MyDrama.entity.Item;
import com.MyDrama.entity.ItemImg;
import com.MyDrama.repository.ItemImgRepository;
import com.MyDrama.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {


    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;


    //상품 저장
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = itemFormDto.createItem();  //상품 등록
        // ItemformDto 객체를 Item 엔티티로 변환
        itemRepository.save(item); // 상품 정보를 DB에 저장
        //이미지 등록
        for(int i = 0;i<itemImgFileList.size();i++){ //업로드한 이미지 개수만큼 반복
            ItemImg itemImg = new ItemImg();  // 새로운 ItemImg 객체 생성
            itemImg.setItem(item);  // itemImg와 item 연결
            if(i==0)  // 대표이미지 설정 만약 i가 0이라면? 대표이미지 설정 아니라면 대표이미지 아님
                itemImg.setRepImgYn("Y");  //0이라면?
                else
                    itemImg.setRepImgYn("N");
                itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));  // 이미지 저장 서비스 호출

        }
        return item.getId();  // 저장된 상품 Id를 반환한다.
    }

    @Transactional(readOnly = true)  // 읽기 전용 (성능 최적화를 위해)
        public ItemFormDto getItemDtl(Long itemId){  // 상품 상세 조회
        //엔티티
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);  // 특정 상품의 상세 정보를 조회하는 메서드
        //디비에서 데이터를 가져온다.

        List<ItemImgDto> itemImgDtoList = new ArrayList<>();  // DTO 변환을 위해 리스트 생성

        for(ItemImg itemImg : itemImgList){  // 상품 이미지 리스트 반복
            // 엔티티 -> dto

            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);  // 엔티티를 DTO로 변환
            itemImgDtoList.add(itemImgDto);  // 변환된 DTO를 리스트에 추가
        }

        //상품 정보 조회(상품 ID로 상품 정보를 조회하는데 없다면 예외 발생한다. )
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        // Item -> ItemFormDto modelMapper
        ItemFormDto itemFormDto = ItemFormDto.of(item);  // 상품 엔티티를 DTO로 변환
        itemFormDto.setItemImgDtoList(itemImgDtoList); //이미지 DTO 리스트를 상품 DTO에 추가
        return itemFormDto;  // 변환된 DTO 반환
    }
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        // 상품 변경
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        // 상품 이미지 변경
        List<Long> itemImgIds = itemFormDto.getItemImgIds(); //수정할 이미지 Id목록 가져오기

        for(int i = 0; i<itemImgFileList.size();i++){  // 이미지 개수만큼 반복
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));  // 이미지 수정 서비스 호출
        }
        return item.getId();  // 수정된 상품 ID 반환
    }


}
