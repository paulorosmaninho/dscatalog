package com.devsuperior.dscatalog.controllers;

import java.net.URI;

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

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	// 2022-02-01 - Refatoracao da paginacao utilizando um objeto pageable
	//	@GetMapping
	//	public ResponseEntity<Page<ProductDTO>> findAll(
	//			@RequestParam(value = "page", defaultValue = "0") Integer page,
	//			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
	//			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
	//			@RequestParam(value = "direction", defaultValue = "ASC") String direction
	//			){
	//
	//		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
	//
	//		Page<ProductDTO> pageDto = productService.findAllPaged(pageRequest);
	//
	//		return ResponseEntity.ok().body(pageDto);
	//	}


	// 2022-02-01 - Refatoracao da paginacao utilizando um objeto pageable
	@GetMapping
	public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable){

		Page<ProductDTO> pageDto = productService.findAllPaged(pageable);

		return ResponseEntity.ok().body(pageDto);
	}



	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id){

		ProductDTO ProductDTO = productService.findById(id);

		return ResponseEntity.ok().body(ProductDTO);

	}



	@PostMapping
	public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO productDTO){

		productDTO = productService.insert(productDTO);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(productDTO.getId())
				.toUri();

		return ResponseEntity.created(uri).body(productDTO);

	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO productDTO){

		productDTO = productService.update(id, productDTO);

		return ResponseEntity.ok().body(productDTO);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){

		productService.delete(id);

		return ResponseEntity.noContent().build();
	}

}
