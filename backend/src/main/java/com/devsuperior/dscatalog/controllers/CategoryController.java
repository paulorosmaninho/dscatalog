package com.devsuperior.dscatalog.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
//@RequestMapping(value) mapeia a rota do recurso. Normalmente no plural.
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	// 2022-02-01 - Refatoracao da paginacao utilizando um objeto pageable
	// @GetMapping
	// public ResponseEntity<Page<CategoryDTO>> findAll(
	// @RequestParam(value = "page", defaultValue = "0") Integer page,
	// @RequestParam(value = "linesPerPage", defaultValue = "12") Integer
	// linesPerPage,
	// @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
	// @RequestParam(value = "direction", defaultValue = "ASC") String direction
	// ){
	//
	// PageRequest pageRequest = PageRequest.of(page, linesPerPage,
	// Direction.valueOf(direction), orderBy);
	//
	// Page<CategoryDTO> pageDto = categoryService.findAllPaged(pageRequest);
	//
	// return ResponseEntity.ok().body(pageDto);
	// }

	// 2022-02-01 - Refatoracao da paginacao utilizando um objeto pageable
	@GetMapping
	public ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable) {

		Page<CategoryDTO> pageDto = categoryService.findAllPaged(pageable);

		return ResponseEntity.ok().body(pageDto);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {

		CategoryDTO categoryDto = categoryService.findById(id);

		return ResponseEntity.ok().body(categoryDto);

	}

	@PostMapping
	public ResponseEntity<CategoryDTO> insert(@Valid @RequestBody CategoryDTO categoryDto) {

		categoryDto = categoryService.insert(categoryDto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(categoryDto.getId())
				.toUri();

		return ResponseEntity.created(uri).body(categoryDto);

	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> update(@Valid @PathVariable Long id, @RequestBody CategoryDTO categoryDto) {

		categoryDto = categoryService.update(id, categoryDto);

		return ResponseEntity.ok().body(categoryDto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		categoryService.delete(id);

		return ResponseEntity.noContent().build();
	}

}
