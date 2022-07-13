package org.mipams.jumbf.jlink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JlinkApplicationTests {
	@Test
	void contextLoads() {
	}

	@Test
	public void mainTest() throws Exception {
		JlinkApplication.main(new String[] {
				"--spring.main.web-environment=false",
				"--spring.autoconfigure.exclude=blahblahblah",
				// Override any other environment properties according to your needs
		});
	}
}
