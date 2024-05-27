package com.example.gachon.domain.notice;

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
@Table(name = "notices")
public class Notices extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;


    @Column(nullable = false)
    private boolean isPinned;
}

