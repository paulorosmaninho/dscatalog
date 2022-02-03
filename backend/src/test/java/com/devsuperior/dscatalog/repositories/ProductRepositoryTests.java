package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() throws Exception {

		// Arrange. Preparar os dados
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados

		// Act. Acoes Necessarias
		productRepository.deleteById(existingId);

		// Assert. Resultado Esperado
		Optional<Product> result = productRepository.findById(existingId);

		// Testa se o objeto esta presente.
		// Se False OK
		// Se True Erro
		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deleteShouldEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		// Assert. Resultado Esperado
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {

			// Arrange. Preparar os dados

			// Act. Acoes Necessarias
			productRepository.deleteById(nonExistingId);
		});

	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdNull() {

		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados
		Product product = Factory.createProduct();
		product.setId(null);

		// Act. Acoes Necessarias
		product = productRepository.save(product);

		// Assert. Resultado Esperado

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());

	}

	@Test
	public void findByIdShouldReturnOptionalProductWhenIdExists() {

		// Exercicio 1 = findById deveria retornar um Optional<Product> não vazio quando
		// o id existir
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados

		// Act. Acoes Necessarias
		Optional<Product> result = productRepository.findById(existingId);

		// Assert. Resultado Esperado
		Assertions.assertTrue(result.isPresent());
	}

	@Test
	public void findByIdShouldReturnOptionalProductNullWhenIdDoesNotExists() {
		// Exercicio 2 = findById deveria retornar um Optional<Product> vazio quando o id não existir
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados

		// Act. Acoes Necessarias
		Optional<Product> result = productRepository.findById(nonExistingId);

		// Assert. Resultado Esperado
		Assertions.assertTrue(result.isEmpty());

	}

}
