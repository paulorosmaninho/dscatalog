package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

	
	//O MockMvc permite simular a chamada das rotas
	//Exemplo: método GET na rota: /products/1
	@Autowired
	private MockMvc mockMvc;

	// Utilizar o MockBean para testes WebMvc
	// Ajuda a simular o comportamento do Service
	@MockBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	// PageImpl é uma classe concreta, que representa uma página de dados.
	private PageImpl<ProductDTO> page;

	private ProductDTO productDto;
	private Long existingId;
	private Long nonExistingId;
	private long dependentId;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;

		productDto = Factory.createProductDTO();

		// Instanciando uma nova page com pelo menos 1 produto na lista
		page = new PageImpl<>(List.of(productDto));

		// Métodos NÃO VOID: Primeiro o WHEN, depois a ação e então o THEN
		Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

		Mockito.when(productService.findById(existingId)).thenReturn(productDto);

		Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		Mockito.when(productService.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any()))
				.thenReturn(productDto);

		Mockito.when(productService.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any()))
				.thenThrow(ResourceNotFoundException.class);

		Mockito.when(productService.insert(ArgumentMatchers.any())).thenReturn(productDto);

		// Métodos VOID: Primeiro a ação e depois o WHEN
		Mockito.doNothing().when(productService).delete(existingId);

		Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);

		// Deletar Id que gera dependência para outra entidade deve retornar Exception
		Mockito.doThrow(DataIntegrityViolationException.class).when(productService).delete(dependentId);

	}

	@Test
	public void findAllShouldReturnPage() throws Exception {

		// Existem duas formas de chamar os métodos do MockMvc:
		// 1 - Encadeando a chamada dos métodos, mas para isso requer conhecimento deles

		// mockMvc.perform(get("/products")).andExpect(status().isOk());

		// 2 - Atribuindo cada resultado de método à uma variável do tipo ResultActions.
		// Esta forma é mais didática.

		ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
	}

	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {

		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		// Acessa o objeto JSON e verifica se existem os campos existem no corpo da
		// resposta
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		// O ObjectMapper converte um objeto JAVA em um string JSON
		String jsonBody = objectMapper.writeValueAsString(productDto);

		// Requisição com PUT precisa passar um parâmetro na URL
		// e os valores no corpo utilizando o método content()
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		// Acessa o objeto JSON e verifica se existem os campos no corpo da resposta
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

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

	@Test
	public void deleteShouldReturnNothingWhenIdExists() throws Exception {

		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());

	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		ResultActions result = mockMvc
				.perform(delete("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void insertShouldReturnProductDTOWhenProductDTOExists() throws Exception {

		// O ObjectMapper converte um objeto JAVA em um string JSON
		String jsonBody = objectMapper.writeValueAsString(productDto);

		// Requisição com POST precisa passar os valores no corpo utilizando o método
		// content()
		ResultActions result = mockMvc.perform(post("/products").content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());

		// Acessa o objeto JSON e verifica se existem os campos no corpo da resposta
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

	}

}
