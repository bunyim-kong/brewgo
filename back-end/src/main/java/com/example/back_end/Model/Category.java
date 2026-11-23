package com.example.back_end.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    private Long id;
    private String name;
    private String slug;
    private int sortOrder;
    private boolean active;
}
