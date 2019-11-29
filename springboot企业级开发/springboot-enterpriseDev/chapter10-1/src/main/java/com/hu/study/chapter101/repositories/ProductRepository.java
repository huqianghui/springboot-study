package com.hu.study.chapter101.repositories;

import com.hu.study.chapter101.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
