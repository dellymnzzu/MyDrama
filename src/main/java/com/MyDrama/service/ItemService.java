package com.MyDrama.service;


import com.MyDrama.dto.ItemFormDto;
import com.MyDrama.entity.Item;
import com.MyDrama.entity.ItemImg;
import com.MyDrama.repository.ItemImgRepository;
import com.MyDrama.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {


    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;








}
