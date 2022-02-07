package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

//Teste de Integração
//Carregar contexto da aplicação utilizando anotação @SpringBootTest
//Nesse teste serão validados o Service e o Repository
//Será feito o Seed no Banco de Dados 
@SpringBootTest

//A anotação @Transactional volta o estado do banco de dados para original
//a cada teste realizado. 
//Exemplo: No teste de deleção será excluído 1 registro. Ao finalizar esse
//teste será feito um Rollback para voltar o banco ao estado original.
@Transactional

//Teste da camada Web
@AutoConfigureMockMvc
public class ProductControllerIT {

	// O MockMvc permite simular a chamada das rotas
	// Exemplo: método GET na rota: /products/1
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private ProductDTO productDto;
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
		productDto = Factory.createProductDTO();

	}

	// Valida se o conteúdo devolvido do Banco
	// e gravado no JSON está ordenado por nome
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/products?page=0&size=12&sort=name,asc").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		// Acessa o objeto JSON e verifica se existem os campos no corpo da resposta
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));

	}

	// Valida se o update realizado está de acordo com o retornado no JSON
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		// O ObjectMapper converte um objeto JAVA em um string JSON
		String jsonBody = objectMapper.writeValueAsString(productDto);

		String expectedName = productDto.getName();
		String expectedDescription = productDto.getDescription();

		// Requisição com PUT precisa passar um parâmetro na URL
		// e os valores no corpo utilizando o método content()
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		// Acessa o objeto JSON e verifica se existem os campos no corpo da resposta
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));

	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		// O ObjectMapper converte um objeto JAVA em um string JSON
		String jsonBody = objectMapper.writeValueAsString(productDto);

		// Requisição com PUT precisa passar um parâmetro na URL
		// e os valores no corpo utilizando o método content()
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());

	}

	
	// Valida se o insert realizado está de acordo com o retornado no JSON
	@Test
	public void insertShouldReturnProductDTOWhenIdExists() throws Exception {

		String expectedName = "Monitor";
		String expectedDescription = "Monitor 22 Polegadas";
		productDto.setName(expectedName);
		productDto.setDescription(expectedDescription);

		// O ObjectMapper converte um objeto JAVA em um string JSON
		String jsonBody = objectMapper.writeValueAsString(productDto);

		// Requisição com POST precisa passar os valores no corpo utilizando o método content()
		ResultActions result = mockMvc.perform(post("/products").content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());

		// Acessa o objeto JSON e verifica se existem os campos no corpo da resposta
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));

	}

	// Valida findById 
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {

		String expectedName = "The Lord of the Rings";
		String expectedDescription = productDto.getDescription();

		// Requisição com GET precisa passar um parâmetro na URL
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		// Acessa o objeto JSON e verifica se existem os campos no corpo da resposta
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").exists());

	}
	
	// Valida findById Exception 
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {
		
		// Requisição com GET precisa passar um parâmetro na URL
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		
	}
	
	
}
