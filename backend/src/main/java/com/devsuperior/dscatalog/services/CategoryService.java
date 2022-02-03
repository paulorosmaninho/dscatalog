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
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

	
//	2022-02-01 - Refatoracao da paginacao utilizando um objeto pageable
//	@Transactional(readOnly = true)
//	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
//		 
//		Page<Category> page = categoryRepository.findAll(pageRequest);
//		
//		Page<CategoryDTO> pageDto = page.map(elementoList -> new CategoryDTO(elementoList));
//		return pageDto;
//	}
	

//	2022-02-01 - Refatoracao da paginacao utilizando um objeto pageable 
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		
		Page<Category> page = categoryRepository.findAll(pageable);
		
		Page<CategoryDTO> pageDto = page.map(elementoList -> new CategoryDTO(elementoList));
		return pageDto;
	}
	
	
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){
		
		Optional<Category> objOptional = categoryRepository.findById(id);
		
		if(!objOptional.isPresent()) {
			throw new ResourceNotFoundException("Categoria " + id + " não encontrada");
		}
		
		Category entity = objOptional.get();
				
		return new CategoryDTO(entity);
	}



	@Transactional
	public CategoryDTO insert(CategoryDTO categoryDto) {
		
		Category entity = new Category();
		entity.setName(categoryDto.getName());
		entity = categoryRepository.save(entity);
		
		return new CategoryDTO(entity);
	}


	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO updatedCategoryDto) {
		
		try {
		Category entity = categoryRepository.getById(id);
//		Category entity = categoryRepository.getOne(id);
		updateData(entity, updatedCategoryDto);
		entity = categoryRepository.save(entity);
		return new CategoryDTO(entity);
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Categoria " + id + " não encontrada");
		}
	}
	

	
	private void updateData(Category entity, CategoryDTO updatedCategoryDto) {
		entity.setName(updatedCategoryDto.getName());
	}



	public void delete(Long id) {

		try {
			categoryRepository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Categoria " + id + " não encontrada");
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}

		
	}

}
