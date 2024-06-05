package com.example.gachon.domain.note;


import com.example.gachon.domain.sentence.Sentences;
import com.example.gachon.domain.user.Users;
import com.example.gachon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notes")
public class Notes extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sentence_id")
    private Sentences sentence;

    @Column(nullable = true)
    private String title;

    @Column(nullable = true)
    private String content;
}
