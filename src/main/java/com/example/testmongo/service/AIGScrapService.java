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
public class AIGScrapService implements CrawlingService {
    @Getter
    private final String company = "AIG";
    private WebDriver webDriver;

    @Override
    public MongoDto scrap() throws InterruptedException {
        webDriver = new ChromeDriver();
        String url = "https://www.aig.co.kr/wo/dpwom010.html?menuId=MS708&prodCd=L0380";
        String birthDay = "19930829";
        String gender = "M";
        String plan = "기본플랜(TM)";
        String paymentTerm = "10년납";
        String insureTerm = "10년만기";
        String paymentCycle = "월납";
        String productName = "무배당 AIG 참 기특한 암보험2309(순수보장형(1종))";

        webDriver.get(url);
        Thread.sleep(3000);
        setBaseInfo(birthDay, gender);
        Thread.sleep(3000);
        setAssureInfo(plan, paymentTerm, insureTerm, paymentCycle);
        Thread.sleep(3000);
        MongoDto mongoDto = crawlTreaty();
        mongoDto.setProductName(productName);
        webDriver.close();
        return mongoDto;
    }

    private void setBaseInfo(String birthDay, String gender) {
        log.info("---- 생년월일 SET ----");
        WebElement birthDayTextBox = webDriver.findElement(By.xpath("//input[@name='brdt']"));
        birthDayTextBox.click();
        birthDayTextBox.clear();
        birthDayTextBox.sendKeys(birthDay);
        log.info("---- 생년월일 SET OK!!! ----");

        log.info("---- 성별 SET ----");
        int genderValue;
        if (gender.equals("M")) genderValue = 1;
        else genderValue = 2;

        WebElement genderRadioButton = webDriver.findElement(By.xpath(String.format("//input[@value=%s]", genderValue)));
        genderRadioButton.click();
        log.info("---- 성별 SET OK!!! ----");

        log.info("---- 확인 Button Click ----");
        WebElement submitButton = webDriver.findElement(By.xpath("//a[@class='btnBlue']"));
        submitButton.click();
        log.info("---- 확인 Button Click OK!!! ----");
    }

    private void setAssureInfo(String plan, String paymentTerm, String insureTerm, String paymentCycle) {
        log.info("---- 플랜 SET ----");
        Select planSelectBox = new Select(webDriver.findElement(By.xpath("//select[@id='prodPlanCd']")));
        planSelectBox.selectByVisibleText(plan);
        log.info("---- 플랜 SET OK!!! ----");

        log.info("---- 납기/보기 SET ----");
        Select payAndInsureTermSelectBox = new Select(webDriver.findElement(By.xpath("//select[@id='paymentPeriod']")));
        payAndInsureTermSelectBox.selectByVisibleText(paymentTerm + " " + insureTerm);
        log.info("---- 납기/보기 SET OK!!! ----");

        log.info("---- 납입 방법 SET ----");
        Select paymentCycleSelectBox = new Select(webDriver.findElement(By.xpath("//select[@id='paymentMethod']")));
        paymentCycleSelectBox.selectByVisibleText(paymentCycle);
        log.info("---- 납입 방법 SET OK!!! ----");
    }

    private MongoDto crawlTreaty() {
        MongoDto mongoDto = new MongoDto();
        HashMap<String, List<String>> mainTreatyInfo = new HashMap<>();
        HashMap<String, HashMap<String, List<String>>> allTreatyInfo = new HashMap<>();
        try {
            List<WebElement> treatyTable = webDriver.findElements(By.xpath("//tbody/tr"));
            for (WebElement treaty : treatyTable) {
                if (treaty.findElement(By.xpath("th")).getText().contains("기본")) {
                    log.info("---- 주계약 정보 Crawl ----");
                    List<WebElement> treatyTemp = treaty.findElements(By.xpath("td"));
                    List<String> mainTreatyName = new ArrayList<>();
                    mainTreatyName.add(treatyTemp.get(0).getText());
                    mainTreatyInfo.put("주계약(특약)명", mainTreatyName);

                    List<String> assureMoneyList = new ArrayList<>();
                    treatyTemp.get(1).findElements(By.xpath("select/option")).forEach(
                            element -> {
                                assureMoneyList.add(element.getText());
                            });
                    mainTreatyInfo.put("가입 금액", assureMoneyList);

                    List<String> paymentTermList = new ArrayList<>();
                    List<String> insureTermList = new ArrayList<>();
                    treatyTemp.get(2).findElements(By.xpath("select/option")).forEach(
                            element -> {
                                String[] splitPayAndInsure = element.getText().split(" ");
                                paymentTermList.add(splitPayAndInsure[0]);
                                insureTermList.add(splitPayAndInsure[1]);
                            });
                    mainTreatyInfo.put("납입 기간", paymentTermList);
                    mainTreatyInfo.put("보험 기간", insureTermList);
                } else {
                    log.info("---- 특약 정보 Crawl ----");
                    HashMap<String, List<String>> oneTreatyInfo = new HashMap<>();
                    List<WebElement> treatyTemp = treaty.findElements(By.xpath("td"));

                    String treatyName = treatyTemp.get(0).getText();
                    List<String> assureMoneyList = new ArrayList<>();
                    treatyTemp.get(1).findElements(By.xpath("select/option")).forEach(
                            element -> {
                                assureMoneyList.add(element.getText());
                            });
                    oneTreatyInfo.put("가입 금액", assureMoneyList);

                    List<String> paymentTermList = new ArrayList<>();
                    List<String> insureTermList = new ArrayList<>();
                    treatyTemp.get(2).findElements(By.xpath("select/option")).forEach(
                            element -> {
                                String[] splitPayAndInsure = element.getText().split(" ");
                                paymentTermList.add(splitPayAndInsure[0]);
                                insureTermList.add(splitPayAndInsure[1]);
                            });
                    oneTreatyInfo.put("납입 기간", paymentTermList);
                    oneTreatyInfo.put("보험 기간", insureTermList);
                    allTreatyInfo.put(treatyName, oneTreatyInfo);
                }
            }
            mongoDto.setMainTreaty(mainTreatyInfo);
            mongoDto.setTreaty(allTreatyInfo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return mongoDto;
    }
}
