package com.example.back_end.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "branches")
public class Branch {
    @Id
    private Long id;
    private String name;
    private String slug;
    private String timezone;
    private String address;
    private boolean active;
    private boolean payOnPickup;
}
