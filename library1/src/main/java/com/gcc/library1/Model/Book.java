package com.gcc.library1.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String author;

    @Column(length = 2000) // 设置简介字段的最大长度
    private String description; // 图书简介
    // Getters and Setters
}