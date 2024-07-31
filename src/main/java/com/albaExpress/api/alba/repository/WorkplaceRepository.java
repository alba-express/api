package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkplaceRepository extends JpaRepository<Workplace, String> {


}
