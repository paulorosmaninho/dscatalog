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

// 27/02/2022 - Essa solução não funciona no PostgreSQL, por isso a solução acima foi implementada.
// Ocorre o erro: ERROR: operator does not exist: bytea = bigint
//	@Query(value = "SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE "
//			+ " (:category IS NULL OR :category IN cats) "
//			+ " AND (LOWER(obj.name) LIKE CONCAT('%',LOWER(:name),'%')"
//			+ " )"
//			)
//	Page<Product> findProductCategory(Pageable pageable, Category category, String name);
	
}
