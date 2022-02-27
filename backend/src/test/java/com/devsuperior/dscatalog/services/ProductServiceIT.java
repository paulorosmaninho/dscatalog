package com.devsuperior.dscatalog.services;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

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
public class ProductServiceIT {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;

	}

	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		// Usar padrao AAA. Arrange/Act/Assert

		// Arrange. Preparar os dados está no @BeforeEach

		// Act. Ações Necessárias
		productService.delete(existingId);

		// Assert. Resultado Esperado
		Assertions.assertEquals(countTotalProducts - 1, productRepository.count());
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
	}

	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() {

		// Puxando a página 0 com tamanho de 10
		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<ProductDTO> productDTO = productService.findAllPaged(pageRequest, 0L, "");

		Assertions.assertFalse(productDTO.isEmpty()); /* Valida se NÃO retornou vazio */
		Assertions.assertEquals(0, productDTO.getNumber()); /* Valida se é a pagina 0 */
		Assertions.assertEquals(10, productDTO.getSize()); /* Valida se o tamanho é 10 */
		Assertions.assertEquals(countTotalProducts, productDTO.getTotalElements()); /* Valida o total de elementos */

	}

	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {

		// Puxando a página 50 com tamanho de 10
		PageRequest pageRequest = PageRequest.of(50, 10);

		Page<ProductDTO> productDTO = productService.findAllPaged(pageRequest, 0L, "");

		Assertions.assertTrue(productDTO.isEmpty()); /* Valida se retornou vazio */

	}

	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortByName() {
		
		// Puxando a página 0 com tamanho de 10
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> productDTO = productService.findAllPaged(pageRequest, 0L, "");
		
		//Validando se o conteúdo retornado do Banco de Dados está ordenado
		Assertions.assertFalse(productDTO.isEmpty()); /* Valida se NÃO retornou vazio */
		Assertions.assertEquals("Macbook Pro", productDTO.getContent().get(0).getName()); /* Valida nome */
		Assertions.assertEquals("PC Gamer", productDTO.getContent().get(1).getName()); /* Valida nome */
		Assertions.assertEquals("PC Gamer Alfa", productDTO.getContent().get(2).getName()); /* Valida nome */
		
	}
	
	
	
	
}
