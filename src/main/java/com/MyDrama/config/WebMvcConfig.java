package com.MyDrama.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    private final VisitorInterceptor visitorInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/image/**")  // 화면에서는 /images/**으로 나온다.
                .addResourceLocations(uploadPath);  //로컬 컴퓨터에서 root 경로를 설정

        registry.addResourceHandler("/banner/**")
                .addResourceLocations("file:///" + bannerLocation + "/");  
        // Linux/Mac의 경우: "file:" + bannerLocation + "/"
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitorInterceptor(visitorService))
              .addPathPatterns("/**");
    }
}
