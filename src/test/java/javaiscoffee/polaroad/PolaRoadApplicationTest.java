package javaiscoffee.polaroad;

import javaiscoffee.polaroad.config.JpaConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {JpaConfigTest.class})
@TestPropertySource(properties = {
		"JWT_SECRET_KEY=3123758a0d7ef02a46cba8bdd3f898dec8afc9f8470341af789d59f3695093be",
		"KAKAO_API_KEY=tabve4120123408dse89749c4123041zvx",
		"KAKAO_REDIRECT_URI=http://localhost:3000/explore",
		"MAIL_PASSWORD=abcd efgh ijkl",
		"REDIS_PASSWORD=a123123!",
		"GOOGLE_CLIENT_ID=ta555412333i40l2s289a111c2223lg4vx",
		"GOOGLE_CLIENT_SECRET=ta555412333i40l2s289a111c2223lg4vx",
		"GOOGLE_REDIRECT_URI=http://localhost:3000/explore",
		})
class PolaRoadApplicationTest {

	@Test
	void contextLoads() {
	}

}
