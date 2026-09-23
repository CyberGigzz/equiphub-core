package com.equiphub.equiphub.repository;

import com.equiphub.equiphub.model.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Long> {
    List<RentalAgreement> findByUserId(Long userId);
}

