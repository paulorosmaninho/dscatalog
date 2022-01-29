package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

	
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		 
		List<Category> list = categoryRepository.findAll();
		
		List<CategoryDTO> listDto = list.stream()
				                   .map(elementoList -> new CategoryDTO(elementoList))
				                   .toList();
		return listDto;
	}
	

	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){
		
		Optional<Category> objOptional = categoryRepository.findById(id);
		
		if(!objOptional.isPresent()) {
			throw new EntityNotFoundException("Categoria " + id + " não encontrada");
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

}
