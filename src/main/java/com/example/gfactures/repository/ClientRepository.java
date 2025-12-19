package com.example.gfactures.repository;

import com.example.gfactures.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}


