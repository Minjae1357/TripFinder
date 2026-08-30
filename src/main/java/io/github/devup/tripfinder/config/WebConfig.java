package io.github.devup.tripfinder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //스프링 설정 클래스로 등록
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // "/uploads/board/**" 로 들어오는 요청을,
        // 실제 로컬 폴더 "uploads/board/" 안의 파일과 연결해줌
        registry.addResourceHandler("/uploads/board/**")
                .addResourceLocations("file:uploads/board/");
    }
}
