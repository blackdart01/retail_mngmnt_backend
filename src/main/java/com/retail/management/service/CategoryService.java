package com.retail.management.service;

import com.retail.management.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category create(Category category);
    Category update(Long id, Category category);
    Optional<Category> getById(Long id);
    List<Category> listAll();
    void delete(Long id);
}
