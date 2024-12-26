package mizdooni.BDD;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/mizdooni/BDD/features",
        glue = "mizdooni.BDD.steps",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class RunCucumberTest {
}
