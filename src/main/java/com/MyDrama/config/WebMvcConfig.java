package com.MyDrama.config;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import com.MyDrama.service.VisitorService;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final VisitorService visitorService;
    @Value("${uploadPath}")  // 프로퍼티에 있는 uploadPath 경로를
    String uploadPath;  // 가지고 있다.
    //uploadPath = "C:/drama  -> 원래 경로지만 //배너 상세 조회(수정을 위해)

    //image/item/XXX.jpg로 나오게 된다.
    // image는 C:/drama라고 생각하면 된다.

    @Value("${bannerLocation}")
    String bannerLocation;

    @Value("${noticeLocation}")
    String noticeLocation;

    @Value("${itemImgLocation}")
    String itemImgLocation;

    @Value("${chromeDriverPath}") // WebDriver 경로 추가
    String chromeDriverPath;

    private final VisitorInterceptor visitorInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/image/**")  // 화면에서는 /images/**으로 나온다.
                .addResourceLocations(uploadPath);  //로컬 컴퓨터에서 root 경로를 설정

        registry.addResourceHandler("/banner/**")
                .addResourceLocations("file:///" + bannerLocation + "/");  
        // Linux/Mac의 경우: "file:" + bannerLocation + "/"

        registry.addResourceHandler("/notice/**")
                .addResourceLocations("file:///" + noticeLocation + "/");

        registry.addResourceHandler("/itemImg/**")
                .addResourceLocations("file:" + itemImgLocation + "/");
    
                
        // 디버깅용 로그
        System.out.println("Item image location: " + itemImgLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitorInterceptor(visitorService))
              .addPathPatterns("/**");
    }

    @Bean
    @Scope("prototype")
    public WebDriver webDriver(){
        System.setProperty("webdriver.chrome.driver",chromeDriverPath);
        //크롬옵션 객체 생성
        ChromeOptions options = new ChromeOptions();

        // 헤드리스 모드 설정
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // ChromeDriver 객체 생성 시 ChromeOptions 적용
        return new ChromeDriver(options);
    }

}
