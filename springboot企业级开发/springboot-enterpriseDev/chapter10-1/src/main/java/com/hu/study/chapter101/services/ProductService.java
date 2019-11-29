package com.hu.study.chapter101.services;

import com.hu.study.chapter101.commands.ProductForm;
import com.hu.study.chapter101.domain.Product;

import java.util.List;

public interface ProductService {

    List<Product> listAll();

    Product getById(Long id);

    Product saveOrUpdate(Product product);

    void delete(Long id);

    Product saveOrUpdateProductForm(ProductForm productForm);

    void sendProductMessage(String id);
}
