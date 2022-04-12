package org.mipams.jumbf.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JumbfApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void mainTest() throws Exception {
		JumbfApplication.main(new String[] {
				"--spring.main.web-environment=false",
				"--spring.autoconfigure.exclude=blahblahblah",
				// Override any other environment properties according to your needs
		});
	}

}
