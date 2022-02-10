package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	// 2022-02-01 - Refatoração da paginação utilizando um objeto pageable
	// @Transactional(readOnly = true)
	// public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
	//
	// Page<Product> page = productRepository.findAll(pageRequest);
	//
	// Page<ProductDTO> pageDto = page.map(elementoList -> new
	// ProductDTO(elementoList, elementoList.getCategories()));
	//
	// return pageDto;
	// }

	// 2022-02-01 - Refatoração da paginação utilizando um objeto pageable
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable) {

		Page<Product> page = productRepository.findAll(pageable);

		Page<ProductDTO> pageDto = page.map(elementoList -> new ProductDTO(elementoList, elementoList.getCategories()));

		return pageDto;
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {

		Optional<Product> objOptional = productRepository.findById(id);

		if (!objOptional.isPresent()) {
			throw new ResourceNotFoundException("Produto " + id + " não encontrado");
		}

		Product entity = objOptional.get();

		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO productDTO) {

		Product entity = new Product();

		copyDtoToEntity(entity, productDTO);

		entity = productRepository.save(entity);

		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO updatedProductDTO) {

		try {

			Product entity = productRepository.getById(id);

			copyDtoToEntity(entity, updatedProductDTO);

			entity = productRepository.save(entity);

			return new ProductDTO(entity, entity.getCategories());

		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Produto " + id + " não encontrado");
		}
	}

	public void delete(Long id) {

		try {
			productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Produto " + id + " não encontrado");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	private void copyDtoToEntity(Product entity, ProductDTO productDTO) {

		// Incluido o setId para funcionar o método save com o Mockito.
		// Retirar depois
		// entity.setId(productDTO.getId());

		entity.setName(productDTO.getName());
		entity.setDescription(productDTO.getDescription());
		entity.setPrice(productDTO.getPrice());
		entity.setImgUrl(productDTO.getImgUrl());
		entity.setMoment(productDTO.getMoment());

		// Limpa lista de categorias da entidade antes de incluir
		// as categorias da lista do DTO
		entity.getCategories().clear();

		// Percorre a lista de categorias do DTO e inclui cada elemento
		// na lista da entidade Categoria que foi instanciada.
		for (CategoryDTO catDto : productDTO.getCategories()) {
			Category category = categoryRepository.getById(catDto.getId());
			entity.getCategories().add(category);
		}

	}

}
