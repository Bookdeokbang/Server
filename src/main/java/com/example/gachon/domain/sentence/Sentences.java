package com.example.gachon.domain.sentence;

import com.example.gachon.domain.history.Histories;
import com.example.gachon.domain.user.Users;
import com.example.gachon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sentences")
public class Sentences extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = true)
    private String difficulty;

    @Column(nullable = true)
    private String grammar;

    @OneToMany(mappedBy = "sentence", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Histories> histories = new HashSet<>();
}
