package com.paymybuddy.app.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.app.DTO.TransferDTO;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransferControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MyUserRepository myUserRepository;

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testTransferPostIntegration() throws Exception {
		MyUser testUser = new MyUser();
		testUser.setFirstName("John");
		testUser.setLastName("Doe");
		testUser.setEmail("john@test.com");
		testUser.setPassword("1234567");
		testUser.setBillingAddress("1 rue de la Paix, Paris");
		testUser.setBalance(new BigDecimal(100));
		testUser.setBankAccountNumber("FR7630001007941234567890185");

		myUserRepository.save(testUser);

		TransferDTO transferDTO = new TransferDTO();
		transferDTO.setContact("test@test.com");
		transferDTO.setAmount(new BigDecimal(50));

		mockMvc.perform(post("/transfer").with(csrf()).flashAttr("transferDTO", transferDTO))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/transfer"));
	}

}
