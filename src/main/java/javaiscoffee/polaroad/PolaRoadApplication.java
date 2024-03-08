package javaiscoffee.polaroad;

import javaiscoffee.polaroad.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Import(JpaConfig.class)
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)	//스프링 시큐리티 기본 로그인 화면 제거
@EnableWebSecurity
public class PolaRoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolaRoadApplication.class, args);
	}

}