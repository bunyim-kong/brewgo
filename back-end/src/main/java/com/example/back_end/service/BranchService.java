package com.example.back_end.service;

import com.example.back_end.Model.dto.BranchSummary;
import java.util.List;

public interface BranchService {
    List<BranchSummary> listActiveBranches();

    boolean isActive(long branchId);
}
