package com.example.demotest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IncomeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    // 종합소득금액
    private double totalIncome;
    // 소득공제
    private double incomeDeduction;
    // 세액공제
    private double taxDeduction;

    // 기본 생성자
    public IncomeInfo() {}

    // 생성자
    public IncomeInfo(String userId, double totalIncome, double incomeDeduction, double taxDeduction) {
        this.userId = userId;
        this.totalIncome = totalIncome;
        this.incomeDeduction = incomeDeduction;
        this.taxDeduction = taxDeduction;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setIncomeDeduction(double incomeDeduction) {
        this.incomeDeduction = incomeDeduction;
    }

    public void setTaxDeduction(double taxDeduction) {
        this.taxDeduction = taxDeduction;
    }
}
