package javaiscoffee.polaroad;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52",
		"KAKAO_API_KEY=tabve4120123408dse89749c4123041zvx",
		"KAKAO_REDIRECT_URI=http://localhost:3000/explore",
		"MAIL_PASSWORD=abcd efgh ijkl",
		"REDIS_PASSWORD=a123123!"
})
class PolaRoadApplicationTest {

	@Test
	void contextLoads() {
	}

}
