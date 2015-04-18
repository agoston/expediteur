package com.bruinproductions;

import com.bruinproductions.bean.TemplateMatchInput;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BaseIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseURL = "http://localhost:8080";

    @Test
    public void testMatch1() {
        TemplateMatchInput input = new TemplateMatchInput("Dear Mr. Bonanza, your package number: NVS297862 is on its way to you, you will receive it on Monday, January 26, 2013 at 12:12, delivery address - Boulevard Prereie 154 Paris 1193SA",
                Arrays.asList(
                        "package\\W+number\\W+(?<packageNumber>\\w{3}\\d{6})",
                        "receive.*on\\W+(?<receiveDate>\\w+, \\w+ \\d+, \\d+) at (?<receiveTime>\\d+:\\d+)"
                        ));

        final LinkedMultiValueMap<String, String> result = restTemplate.postForObject(baseURL + "/templates/find", input, LinkedMultiValueMap.class);
        assertNotNull(result);
        assertThat(result.size(), is(3));
        assertThat(result.getFirst("packageNumber"), is("NVS297862"));
        assertThat(result.getFirst("receiveDate"), is("Monday, January 26, 2013"));
        assertThat(result.getFirst("receiveTime"), is("12:12"));
    }

    @Test
    public void testMatch2() {
        TemplateMatchInput input = new TemplateMatchInput("Hello Mr. Raphael Doona, We have successfully shipped your package to - mount laurel New Jersey 1921LK NY, where it arrives on Sunday 21 February 2013 at 10:00 AM, reservation number 28273642",
                Arrays.asList(
                        "reservation number (?<reservationNumber>\\d+)",
                        "hello (?<name>.+?),"
                ));

        final LinkedMultiValueMap<String, String> result = restTemplate.postForObject(baseURL + "/templates/find", input, LinkedMultiValueMap.class);
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.getFirst("reservationNumber"), is("28273642"));
        assertThat(result.getFirst("name"), is("Mr. Raphael Doona"));
    }

    // TODO
    @Test
    @Ignore("Fix the multimatch case - java regex is fubar, we'll have to implement our own named group handling")
    public void testMultiMatch() {
        TemplateMatchInput input = new TemplateMatchInput("Hello Mr. Raphael Doona, Hello Mrs. Who Ever, We have successfully shipped your package to - mount laurel New Jersey 1921LK NY, where it arrives on Sunday 21 February 2013 at 10:00 AM, reservation number 28273642 for you and reservation number 123456 for your dear wife",
                Arrays.asList(
                        "reservation number (?<reservationNumber>\\d+)",
                        "hello (?<name>.+?),"
                ));

        final LinkedMultiValueMap<String, String> result = restTemplate.postForObject(baseURL + "/templates/find", input, LinkedMultiValueMap.class);
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get("reservationNumber"), is("28273642"));
        assertThat(result.get("name"), is("Mr. Raphael Doona"));
    }

}
