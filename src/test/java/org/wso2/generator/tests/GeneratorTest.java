package org.wso2.generator.tests;

import org.testng.annotations.Test;
import org.wso2.generator.HtmlGenerator;

import java.io.IOException;

public class GeneratorTest {
    @Test(description = "Test if the HTMLGenerator class is working.")
    public void testHTMLGenerator() throws IOException {
        HtmlGenerator htmlGenerator = new HtmlGenerator();
        htmlGenerator.init();
    }
}
