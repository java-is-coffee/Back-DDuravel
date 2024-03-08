package javaiscoffee.polaroad;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"jwt.secret=3123758a0d7ef02a46cba8bdd3f898dec8afc9f8470341af789d59f3695093be"
})
class PolaRoadApplicationTest {

	@Test
	void contextLoads() {
	}

}
