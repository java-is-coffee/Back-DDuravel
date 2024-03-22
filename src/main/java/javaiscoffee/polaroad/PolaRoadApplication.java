package javaiscoffee.polaroad;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import javaiscoffee.polaroad.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Import(JpaConfig.class)
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)	//스프링 시큐리티 기본 로그인 화면 제거
@OpenAPIDefinition(
		servers = {
				@Server(url = "https://k951a463f2f5fa.user-app.krampoline.com", description = "Default Server url")
		}
)
@EnableWebSecurity
public class PolaRoadApplication {

	public static void main(String[] args) {
		// 크램폴린 이메일 프록시 서버 설정
		// System.setProperty("mail.smtp.proxy.host", "krmp-proxy.9rum.cc");
		// System.setProperty("mail.smtp.proxy.port", "3128");
        System.setProperty("http.proxyHost", "krmp-proxy.9rum.cc");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1|10.*.*.*");
        System.setProperty("https.proxyHost", "krmp-proxy.9rum.cc");
        System.setProperty("https.proxyPort", "3128");
        System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1|10.*.*.*");
        System.setProperty("java.net.useSystemProxies", "true");
		SpringApplication.run(PolaRoadApplication.class, args);
	}

}
