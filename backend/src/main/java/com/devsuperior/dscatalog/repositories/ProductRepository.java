package com.devsuperior.dscatalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

//	Faz a consulta com JPQL para selecionar produtos por categoria
//	Utiliza JOIN explicito com categoria e filtra com cláusula WHERE e IN

//	Refatorado em 27/02/2022 com a solução do erro: ERROR: operator does not exist: bytea = bigint	
	@Query(value = "SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE "
			+ " (COALESCE(:categories) IS NULL OR cats IN :categories) "
			+ " AND (LOWER(obj.name) LIKE CONCAT('%',LOWER(:name),'%')"
			+ " )"
			)
	Page<Product> findProductCategory(Pageable pageable, List<Category> categories, String name);
	
	
	//Acrescentada consulta para buscar Categorias e resolver o problema das N+1 Consultas
	//As categorias ficarão em memória e o JPA vai orquestrar a utilização delas
	//JOIN FECTH não aceita objetos Pageable, por isso é preciso fazer duas consultas
	@Query(value = "SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")
	List<Product> findProductsWithCategories(List<Product> products);
	

// 27/02/2022 - Essa solução não funciona no PostgreSQL, por isso a solução acima foi implementada.
// Ocorre o erro: ERROR: operator does not exist: bytea = bigint
//	@Query(value = "SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE "
//			+ " (:category IS NULL OR :category IN cats) "
//			+ " AND (LOWER(obj.name) LIKE CONCAT('%',LOWER(:name),'%')"
//			+ " )"
//			)
//	Page<Product> findProductCategory(Pageable pageable, Category category, String name);
	
}
