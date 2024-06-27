package com.example.demotest.service;

import com.example.demotest.entity.IncomeInfo;
import com.example.demotest.entity.Member;
import com.example.demotest.repository.IncomeInfoRepository;
import com.example.demotest.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.Arrays;

@Service
public class UserInfoService {

    @Value("${scrap.url}")
    private String url;
    @Value("${scrap.X-API-KEY}")
    private String key;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private IncomeInfoRepository incomeInfoRepository;
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Object> getScrappingData(String userId) {
        Member member = memberRepository.findMemberByUserId(userId);
        if (member != null) {
            IncomeInfo IncomeInfo = new IncomeInfo();
            JsonNode list = callScrappingData(member.getName(), member.getRegNo());
            System.out.println(list);
            IncomeInfo.setUserId(userId);
            // 종합소득금액
            IncomeInfo.setTotalIncome(list.path("종합소득금액").asDouble());
            // 국민연금 공제액의 합
            double deduction1 = 0.0;
            JsonNode deduction_arr = list.path("소득공제").path("국민연금");
            for (JsonNode node : deduction_arr) {
                String value = node.path("공제액").asText().replace(",", "");
                deduction1 += Double.parseDouble(value); // 정수로 변환
            }
            // 신용카드 소득공제의 합
            double deduction2 = 0.0;
            JsonNode deduction2_arr = list.path("소득공제").path("신용카드소득공제").path("month");
            for (JsonNode node : deduction2_arr) {
                String[] values = node.elements().next().asText().split("\\.");
                deduction2 += Double.parseDouble(values[0].replace(",", ""));
            }
            // 소득공제
            IncomeInfo.setIncomeDeduction(deduction1 + deduction2);
            // 세액공제
            String taxDeduction = list.path("소득공제").path("세액공제").asText();
            IncomeInfo.setTaxDeduction(Double.parseDouble(taxDeduction.replace(",", "")));
            // db 저장
            incomeInfoRepository.save(IncomeInfo);
            return ResponseEntity.ok("Scrapping and Save successful for user: " + userId);
        } else {
            return ResponseEntity.ok("Fail scrapping and Save for user: " + userId);
        }
    }

    public JsonNode callScrappingData (String name, String regNo) {
        // HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", key);
        // Body
        String jsonBody = "{\"name\": \"" + name + "\", \"regNo\": \"" + regNo + "\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode dataNode = root.get("data");
                if (dataNode != null) {
                    return dataNode;
                } else {
                    System.err.println("No data field");
                }
            } else {
                System.err.println("Fail to post " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("An error occurred while scrapping data, because of" +e);
        }
        return null;
    }

    public Object getRefundPrice(String userId) {
        IncomeInfo incomeInfo = incomeInfoRepository.findIncomeInfoByUserId(userId);
        if (incomeInfo != null) {
            // 과세표준
            double taxBase = incomeInfo.getTotalIncome() - incomeInfo.getIncomeDeduction();
            // 산출세액
            double taxAmount = 0.0;
            // 결정세액
            int determinedTax = 0;

            if (taxBase <= 14000000) {
                taxAmount = taxBase * 0.06;
            } else if (taxBase > 14000000 && taxBase <= 50000000) {
                taxAmount = 840000 + ((taxBase - 14000000) * 0.15);
            } else if (taxBase > 50000000 && taxBase <= 88000000) {
                taxAmount = 6240000 + ((taxBase - 50000000) * 0.24);
            } else if (taxBase > 88000000 && taxBase <= 150000000) {
                taxAmount = 15360000 + ((taxBase - 88000000) * 0.35);
            } else if (taxBase > 150000000 && taxBase <= 300000000) {
                taxAmount = 37060000 + ((taxBase - 150000000) * 0.38);
            } else if (taxBase > 300000000 && taxBase <= 500000000) {
                taxAmount = 94060000 + ((taxBase - 300000000) * 0.4);
            } else if (taxBase > 500000000 && taxBase <= 1000000000) {
                taxAmount = 174060000 + ((taxBase - 500000000) * 0.42);
            } else if (taxBase > 1000000000) {
                taxAmount = 384060000 + ((taxBase - 1000000000) * 0.45);
            }
            determinedTax = (int) Math.round(taxAmount - incomeInfo.getTaxDeduction());

            // 천 단위로 포맷
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            return numberFormat.format(determinedTax);
        } 
        else {
            System.out.println("No data field");
            return null;
        }
    }
}
