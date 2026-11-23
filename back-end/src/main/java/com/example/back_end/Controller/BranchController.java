package com.example.back_end.Controller;

import com.example.back_end.Model.dto.BranchSummary;
import com.example.back_end.service.BranchService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BranchController {
    private final BranchService branches;

    BranchController(BranchService branches) {
        this.branches = branches;
    }

    @GetMapping("/api/v1/branches")
    List<BranchSummary> branches() {
        return branches.listActiveBranches();
    }
}
