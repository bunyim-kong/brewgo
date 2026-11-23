package com.example.back_end.Repository;

import com.example.back_end.Model.Branch;
import com.example.back_end.Model.dto.BranchSummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("""
            SELECT new com.example.back_end.Model.dto.BranchSummary(b.id, b.name, b.slug, b.timezone, b.address, b.payOnPickup)
            FROM Branch b WHERE b.active = true ORDER BY b.name, b.id
            """)
    List<BranchSummary> findPublicBranches();

    boolean existsByIdAndActiveTrue(Long id);
}
