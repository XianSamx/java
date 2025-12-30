package com.gcc.library1.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "borrowHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Long userId;

    private Long bookId;

    private LocalDate Date;

    @Column(nullable = false)
    private String behavour;
}
