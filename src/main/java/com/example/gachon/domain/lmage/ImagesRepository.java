package com.example.gachon.domain.lmage;

import com.example.gachon.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

    List<Images> findByUser (Users user);
}
