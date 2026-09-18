package com.equiphub.equiphub.repository;

import com.equiphub.equiphub.model.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Long> {
    List<RentalAgreement> findByUserId(Long userId);
}

