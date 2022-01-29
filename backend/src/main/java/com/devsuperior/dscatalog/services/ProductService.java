package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	
	@Autowired
	private ProductRepository productRepository;

	
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		 
		Page<Product> page = productRepository.findAll(pageRequest);
		
		Page<ProductDTO> pageDto = page.map(elementoList -> new ProductDTO(elementoList));
		return pageDto;
	}
	

	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id){
		
		Optional<Product> objOptional = productRepository.findById(id);
		
		if(!objOptional.isPresent()) {
			throw new ResourceNotFoundException("Produto " + id + " não encontrado");
		}
		
		Product entity = objOptional.get();
				
		return new ProductDTO(entity, entity.getCategories());
	}



	@Transactional
	public ProductDTO insert(ProductDTO ProductDTO) {
		
		Product entity = new Product();
		entity.setName(ProductDTO.getName());
		entity.setDescription(ProductDTO.getDescription());
		entity.setPrice(ProductDTO.getPrice());
		entity.setImgUrl(ProductDTO.getImgUrl());
		entity.setMoment(ProductDTO.getMoment());
		
		entity = productRepository.save(entity);
		
		return new ProductDTO(entity);
	}


	
	@Transactional
	public ProductDTO update(Long id, ProductDTO updatedProductDTO) {
		
		try {
		Product entity = productRepository.getById(id);
		updateData(entity, updatedProductDTO);
		entity = productRepository.save(entity);
		return new ProductDTO(entity);
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Produto " + id + " não encontrado");
		}
	}

	
	private void updateData(Product entity, ProductDTO updatedProductDTO) {
		entity.setName(updatedProductDTO.getName());
		entity.setDescription(updatedProductDTO.getDescription());
		entity.setPrice(updatedProductDTO.getPrice());
		entity.setImgUrl(updatedProductDTO.getImgUrl());
		entity.setMoment(updatedProductDTO.getMoment());
	}


	public void delete(Long id) {

		try {
			productRepository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Produto " + id + " não encontrado");
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}

		
	}

}
