package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WageRepository extends JpaRepository<Wage, String>, WageRepositoryCustom {
}
