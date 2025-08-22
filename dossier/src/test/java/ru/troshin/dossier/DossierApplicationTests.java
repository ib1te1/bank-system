package ru.troshin.dossier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.troshin.dossier.service.MailService;

@SpringBootTest
class DossierApplicationTests {

	@MockitoBean
	private MailService mailService;

	@Test
	void contextLoads() {
	}

}
