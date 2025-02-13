package com.MyDrama.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${uploadPath}")  // 프로퍼티에 있는 uploadPath 경로를
    String uploadPath;  // 가지고 있다.
    //uploadPath = "C:/drama  -> 원래 경로지만
    //image/item/XXX.jpg로 나오게 된다.
    // image는 C:/drama라고 생각하면 된다.

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/image/**")  // 화면에서는 /images/**으로 나온다.
                .addResourceLocations(uploadPath);  //로컬 컴퓨터에서 root 경로를 설정
    }

}
