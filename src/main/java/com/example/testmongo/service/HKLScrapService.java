package com.example.testmongo.service;

import com.example.testmongo.MongoDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class HKLScrapService implements CrawlingService {
    @Getter
    private final String company = "HKL";

    private WebDriver webDriver;

    @Override
    public MongoDto scrap() throws InterruptedException {
        webDriver = new ChromeDriver();
        String url = "https://www.heungkuklife.co.kr/front/product/productPlanForm.do?cd_prt=G01772&cd_prts=P003&prt_num_seq=1344";
        String name = "김아무개";
        String birthDay = "19930829";
        String gender = "M";
        String productKind = "2종(납입면제형)";
        String productName = "(무)흥국생명 다(多)사랑 THE간편건강보험(갱신형)";
        MongoDto mongoDto = new MongoDto();

        webDriver.get(url);
        Thread.sleep(3000);
        setBaseInfo(name, birthDay, gender);
        Thread.sleep(3000);
        setAssureInfo(productKind);
        Thread.sleep(3000);
        crawlMainTreaty(mongoDto);
        Thread.sleep(3000);
        crawlTreaty(mongoDto);

        mongoDto.setProductName(productName);
        webDriver.close();
        return mongoDto;
    }

    private void setBaseInfo(String name, String birthDay, String gender) {
        log.info("---- 이름 SET ----");
        WebElement nameTextBox = webDriver.findElement(By.xpath("//input[@title='이름 입력']"));
        nameTextBox.click();
        nameTextBox.clear();
        nameTextBox.sendKeys(name);
        log.info("---- 이름 SET OK!!! ----");

        log.info("---- 성별 SET ----");
        int genderValue;
        if (gender.equals("M")) genderValue = 1;
        else genderValue = 2;
        WebElement genderRadioButton = webDriver.findElement((By.xpath(String.format("//input[@value='%d']", genderValue))));
        genderRadioButton.click();
        log.info("---- 성별 SET OK!!! ----");

        log.info("---- 생년월일 SET ----");
        WebElement birthDayTextBox = webDriver.findElement(By.xpath("//input[@title='생년월일 입력']"));
        birthDayTextBox.click();
        birthDayTextBox.clear();
        birthDayTextBox.sendKeys(birthDay);
        log.info("---- 생년월일 SET OK!!! ----");
    }

    private void setAssureInfo(String productKind) {
        log.info("---- 보험 종류 SET ----");
        Select productKindSelectBox = new Select(webDriver.findElement(By.xpath("//select[@title='주보험종류 선택']")));
        productKindSelectBox.selectByVisibleText(productKind);
        log.info("---- 보험 종류 SET OK!!! ----");
    }

    private void crawlMainTreaty(MongoDto mongoDto) {
        HashMap<String, List<String>> mainTreatyInfo = new HashMap<>();
        try {
            log.info("---- 주계약 정보 Crawl ----");

            log.info("---- 보험 종류 Crawl ----");
            List<String> productKindList = new ArrayList<>();
            webDriver.findElements(By.xpath("//select[@title='주보험종류 선택']/option")).forEach(
                    element -> {
                        productKindList.add(element.getText());
                    }
            );
            mainTreatyInfo.put("보험 종류", productKindList);
            log.info("---- 보험 종류 Crawl OK!!! ----");

            log.info("---- 보험 기간 Crawl ----");
            List<String> mainTreatyInsureTermList = new ArrayList<>();
            webDriver.findElements(By.xpath("//select[@title='주보험기간 선택']/option")).forEach(
                    element -> {
                        mainTreatyInsureTermList.add(element.getText());
                    }
            );
            mainTreatyInfo.put("보험 기간", mainTreatyInsureTermList);
            log.info("---- 보험 기간 Crawl OK!!! ----");

            log.info("---- 납입 기간 Crawl ----");
            List<String> mainTreatyPaymentTermList = new ArrayList<>();
            webDriver.findElements(By.xpath("//select[@title='주보험 납입기간 선택']/option")).forEach(
                    element -> {
                        mainTreatyPaymentTermList.add(element.getText());
                    }
            );
            mainTreatyInfo.put("납입 기간", mainTreatyPaymentTermList);
            log.info("---- 납입 기간 Crawl OK!!! ----");

            log.info("---- 납입 주기 Crawl ----");
            List<String> mainTreatyPaymentCycleList = new ArrayList<>();
            webDriver.findElements(By.xpath("//select[@title='주보험 납입주기 선택']/option")).forEach(
                    element -> {
                        mainTreatyPaymentCycleList.add(element.getText());
                    }
            );
            log.info("---- 납입 주기 Crawl OK!!! ----");

            log.info("---- 가입 금액 Crawl ----");
            List<String> mainTreatyAssureMoneyList = new ArrayList<>();
            mainTreatyAssureMoneyList.add("100만원");
            mainTreatyAssureMoneyList.add("1000만원");
            log.info("---- 가입 금액 Crawl OK!!! ----");

            mongoDto.setMainTreaty(mainTreatyInfo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void crawlTreaty(MongoDto mongoDto) {
        HashMap<String, HashMap<String, List<String>>> allTreatyInfo = new HashMap<>();
        List<WebElement> treatyTable = webDriver.findElements(By.xpath("//tbody[@id='specList']/tr"));

        for (WebElement treaty : treatyTable) {
            HashMap<String, List<String>> oneTreatyInfo = new HashMap<>();
            List<WebElement> tdList = treaty.findElements(By.xpath("td"));
            String treatyName = tdList.get(1).findElement(By.xpath("div")).getText();
            log.info("---- {} ----", treatyName);
            log.info("---- 보험 기간 Crawl ----");
            List<String> insureTermList = new ArrayList<>();
            tdList.get(2).findElements(By.xpath("div/select/option")).forEach(
                    element -> {
                        insureTermList.add(element.getText());
                    }
            );
            log.info("---- 보험 기간 Crawl OK!!! ----");

            log.info("---- 납입 기간 Crawl ----");
            List<String> paymentTermList = new ArrayList<>();
            tdList.get(3).findElements(By.xpath("div/select/option")).forEach(
                    element -> {
                        paymentTermList.add(element.getText());
                    }
            );
            log.info("---- 납입 기간 Crawl OK!!! ----");

            log.info("---- 가입 금액 Crawl ----");
            List<String> assureMoneyList = new ArrayList<>();
            assureMoneyList.add("100만원");
            assureMoneyList.add("1000만원");
            log.info("---- 가입 금액 Crawl OK!!! ----");

           oneTreatyInfo.put("보험 기간", insureTermList);
           oneTreatyInfo.put("납입 기간", paymentTermList);
           oneTreatyInfo.put("가입 금액", assureMoneyList);
           allTreatyInfo.put(treatyName, oneTreatyInfo);
           log.info("---- {} OK!!! ----", treatyName);
        }
        mongoDto.setTreaty(allTreatyInfo);
    }
}
