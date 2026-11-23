package com.example.back_end.Model.dto;

public record BranchSummary(long id, String name, String slug, String timezone, String address, boolean payOnPickup) {}
