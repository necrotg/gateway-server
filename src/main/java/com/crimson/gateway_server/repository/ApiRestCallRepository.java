package com.crimson.gateway_server.repository;

import com.crimson.gateway_server.model.ApiRestCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApiRestCallRepository extends JpaRepository<ApiRestCall, UUID> {
}
