package javaiscoffee.polaroad;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import javaiscoffee.polaroad.config.JpaConfig;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Import(JpaConfig.class)
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)	//스프링 시큐리티 기본 로그인 화면 제거
@OpenAPIDefinition(
		servers = {
				@Server(url = "https://polaroad.shop", description = "Default Server url")
		}
)
@EnableWebSecurity
public class PolaRoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolaRoadApplication.class, args);
	}

}
