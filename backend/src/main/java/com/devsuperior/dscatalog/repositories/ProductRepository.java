package com.devsuperior.dscatalog.repositories;

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
	
	@Query(value = "SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE "
			+ " (:category IS NULL OR :category IN cats) ")
	Page<Product> findProductCategory(Pageable pageable, Category category);
	
	
//	Teste. Substituir OR :category por OR NULL e funcionou
	
//	@Query(value = "SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE "
//			+ " (:category IS NULL OR NULL IN cats) ")
//	Page<Product> findProductCategory(Pageable pageable, Category category);
	
}
