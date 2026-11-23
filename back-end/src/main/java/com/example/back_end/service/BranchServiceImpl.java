package com.example.back_end.service;

import com.example.back_end.Model.dto.BranchSummary;
import com.example.back_end.Repository.BranchRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class BranchServiceImpl implements BranchService {
    private final BranchRepository branches;

    BranchServiceImpl(BranchRepository branches) {
        this.branches = branches;
    }

    @Override
    public List<BranchSummary> listActiveBranches() {
        return branches.findPublicBranches();
    }

    @Override
    public boolean isActive(long branchId) {
        return branches.existsByIdAndActiveTrue(branchId);
    }
}
