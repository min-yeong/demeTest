package com.example.demotest.repository;

import com.example.demotest.entity.IncomeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeInfoRepository extends JpaRepository<IncomeInfo, Long> {

    IncomeInfo findIncomeInfoByUserId(String UserId);

}
