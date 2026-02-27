// mbcFinalProject1 - com.mbc.mid - WebConfigurer.java
package com.mbc.fin1;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {
   
   /*@Override   // 접근 권한 부여
   public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**").allowedOrigins("*");
      //registry.addMapping("/**").allowedOrigins("http://localhost:3000");
   }*/
   
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
       // 현재 프로젝트 폴더 내 "images" 폴더의 절대 경로를 가져옵니다.
       // (예: file:///C:/springBootSample/middleProject/images/)
       String uploadPath = Paths.get("images").toAbsolutePath().toUri().toString();

       // 주소창에 /images/** 로 요청이 오면 실제 폴더 위치를 보여줍니다.
       registry.addResourceHandler("/images/**")
               .addResourceLocations(uploadPath);
   }
   
}