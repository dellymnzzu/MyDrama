package com.MyDrama.service;


import com.MyDrama.dto.ItemCrawlerDto;
import com.MyDrama.entity.ItemCrawl;
import com.MyDrama.repository.ItemCrawlerRepository;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor

public class WebCrawlerService {

    private final ItemCrawlerRepository itemCrawlerRepository;

    public void crawl(){
        ChromeDriver webDriver = new ChromeDriver();
        String url = "https://manyo.co.kr/goods/goods_list.php?cateCd=002001";

        webDriver.get(url);
        
        // 디버깅 출력
        System.out.println("페이지 접속 완료: " + url);
        
        // 페이지 로딩 대기
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
        
        // 상품 목록이 로드될 때까지 대기 (실제 사이트의 클래스명 사용)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".goods_list_cont")));
        System.out.println("상품 목록 영역 로드 완료");
        
        // 상품 목록 가져오기
        List<WebElement> items = webDriver.findElements(By.cssSelector(".item_cont"));
        
        System.out.println("찾은 상품 수: " + items.size());
        
        for (WebElement item : items) {
            try {
                ItemCrawl itemCrawl = new ItemCrawl();
                
                // 상품명: 실제 사이트에서 확인한 클래스 적용
                itemCrawl.setName(item.findElement(By.cssSelector(".item_name")).getText());
                System.out.println("상품명: " + itemCrawl.getName());
                
                // 가격: 실제 사이트의 가격 정보 클래스 적용
                String price = "";
            
                    // 할인가가 있는 경우
                    price = item.findElement(By.cssSelector(".item_money_box .item_price")).getText();
                    itemCrawl.setPrice(price);
                System.out.println("가격: " + price);
                 
                
                
                // 이미지: 실제 이미지 태그 위치 적용
                itemCrawl.setImgUrl(item.findElement(By.cssSelector(".item_photo_box .middle")).getAttribute("src"));
                System.out.println("이미지 URL: " + itemCrawl.getImgUrl());
                
                saveItem(itemCrawl);
            } catch (Exception e) {
                System.out.println("상품 처리 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("크롤링 작업 완료");
        webDriver.quit();
    }
    public void saveItem(ItemCrawl item){
        itemCrawlerRepository.save(item);
    }

}

