package com.aipply.aipply.repository;

import com.aipply.aipply.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByName(String name);
    User findByEmail(String email);

    long countByRequestForCompanyTrue();

    User findUserById(Integer id);
}

