package com.devsuperior.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;

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

}
