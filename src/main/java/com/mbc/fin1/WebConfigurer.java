// mbcFinalProject1 - com.mbc.fin1 - WebConfigurer.java
package com.mbc.fin1;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

   // application.properties에서 주입 (프로젝트 루트/images)
   @Value("${app.upload.dir}")
   private String uploadDir;

   @Override
   public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
      .allowedOrigins("http://localhost:5173") // "*" 대신 프론트 주소를 정확히 명시
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS 꼭 포함
      .allowedHeaders("*")
      .allowCredentials(true) // 세션 받을 준비 됐다 라고 선언
      .maxAge(3600); // 간보기(Preflight) 결과를 1시간 동안 기억하게 함
   }

   @Override
   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      // OS별 경로 구분자 문제 해결 (Windows \ → URI 변환)
      String imageUri = Paths.get(uploadDir).toUri().toString();
      registry.addResourceHandler("/images/**")
          .addResourceLocations(imageUri);
   }
}