package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServicesTests {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private long existingCategoryId;
	private long nonExistingCategoryId;
	private long dependentId;

	// PageImpl é uma classe concreta, que representa uma página de dados.
	private PageImpl<Product> page;

	private Product product;
	private Category category;
	private ProductDTO productDto;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		existingCategoryId = 2L;
		nonExistingCategoryId = 2000L;
		dependentId = 4L;
		product = Factory.createProduct();
		productDto = Factory.createProductDTO();
		category = Factory.createCategory();

		// Instanciando uma nova page com pelo menos 1 produto na lista
		page = new PageImpl<>(List.of(product));

		// Configurando a simulação do comportamento do método
		// deleteById do Repository no Mockito. O que deveria acontecer.
		// Métodos VOID: Primeiro a ação e depois o WHEN

		// Deletar Id que existe não deve retornar nada
		Mockito.doNothing().when(productRepository).deleteById(existingId);

		// Deletar Id que não existe deve retornar Exception
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);

		// Deletar Id que gera dependência para outra entidade deve retornar Exception
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

		// Configurando a simulação do comportamento dos métodos
		// findAll, save e findById do Repository no Mockito. O que deveria acontecer.
		// Métodos NÃO VOID: Primeiro o WHEN, depois a ação e então o THEN

		// Traduzindo o comando abaixo:
		// Quando (WHEN) o método productRepository.findAll for acionado
		// Passando um objeto qualquer ((Pageable)ArgumentMatchers.any()), ele será
		// covertido para Pageable
		// Então (THEN) retornará uma página (page) de produto
		Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

		// Método save retorna
		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
		// Mockito.when(productRepository.save(product)).thenReturn(product);

		// Método findById com Id existente para retornar um Optional preenchido
		Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));

		// Método findById com Id inexistente para retornar um Optional vazio
		Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		// Método getById com Id existente para retornar um produto
		Mockito.when(productRepository.getById(existingId)).thenReturn(product);
		// Mockito.when(productRepository.getOne(existingId)).thenReturn(product);

		// Método getById com Id inexistente para retornar uma exception
		Mockito.when(productRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		// Mockito.when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		// Método getById com Id existente para retornar uma categoria
		Mockito.when(categoryRepository.getById(existingCategoryId)).thenReturn(category);
		// Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);

		// Método getById com Id inexistente para retornar uma exception
		Mockito.when(categoryRepository.getById(nonExistingCategoryId)).thenThrow(EntityNotFoundException.class);
		// Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Assert. Resultado Esperado
		Assertions.assertDoesNotThrow(() -> {
			// Act. Ações Necessárias

			productService.delete(existingId);

		});

		// Mockito verifica se o metodo do Repository foi chamado e quantas vezes.
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Assert. Resultado Esperado
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			// Act. Ações Necessárias

			productService.delete(nonExistingId);

		});

		// Mockito verifica se o metodo do Repository foi chamado e quantas vezes.
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExists() {
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Assert. Resultado Esperado

		Assertions.assertThrows(DatabaseException.class, () -> {

			// Act. Ações Necessárias

			productService.delete(dependentId);
		});

		// Mockito verifica se o metodo do Repository foi chamado e quantas vezes.
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
	}

	@Test
	public void findAllPagedShouldReturnPage() {
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Act. Ações Necessárias
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = productService.findAllPaged(pageable);

		// Assert. Resultado Esperado
		Assertions.assertNotNull(result);
		Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);

	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Act. Ações Necessárias
		productDto = productService.findById(existingId);

		// Assert. Resultado Esperado
		Assertions.assertNotNull(productDto);
		Mockito.verify(productRepository, Mockito.times(1)).findById(existingId);

	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Assert. Resultado Esperado
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			// Act. Ações Necessárias
			productService.findById(nonExistingId);
		});

		Mockito.verify(productRepository, Mockito.times(1)).findById(nonExistingId);

	}

//	@Test
//	 public void insertShouldReturnProductDTOWhenProductExists() {
//	 // Usar padrao AAA. Arrange/Act/Assert
//	
//	 // Arrange. Preparar os dados está no @BeforeEach
//	 ProductDTO productDto = Factory.createProductDTO();
//	
//	 // Act. Ações Necessárias
//	 productDto = productService.insert(productDto);
//	
//	 // Assert. Resultado Esperado
//	 Assertions.assertNotNull(productDto);
//	
//	 // O verify para o método save só funciona se 
//	 // o Id não for perdido na classe ProductService
//
//	 // Foi preciso adicionar o Id no método copyDtoToEntity
//	 Mockito.verify(productRepository, Mockito.times(1)).save(product);
//	
//	 }

	@Test
	public void updateShouldReturnProductDTOWhenProductIdExists() {
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Act. Ações Necessárias
		productDto = productService.update(existingId, productDto);

		// Assert. Resultado Esperado
		Assertions.assertNotNull(productDto);

		Mockito.verify(productRepository, Mockito.times(1)).save(product);

	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		// Usar padrao AAA. Arrange/Act/Assert

		// Assert. Resultado Esperado
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			// Arrange. Preparar os dados está no @BeforeEach

			// Act. Ações Necessárias
			productDto = productService.update(nonExistingId, productDto);

			// Assert. Resultado Esperado
			Assertions.assertNotNull(productDto);
		});

	}

}
