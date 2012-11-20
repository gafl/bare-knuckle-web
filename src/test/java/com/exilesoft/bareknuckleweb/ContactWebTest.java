package com.exilesoft.bareknuckleweb;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ContactWebTest {

    @Test
    public void shouldFindSavedContacts() throws Exception {
        Server server = new Server(0);
        server.setHandler(new WebAppContext("src/main/webapp", "/phonebook"));
        server.start();

        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:integration");
        DataSources.setDataSource(dataSource);
        try (Transaction tx = DataSources.begin()) {
            new JdbcContactStorage().createTable();
        }



        int localPort = server.getConnectors()[0].getLocalPort();

        String url = "http://localhost:" + localPort + "/phonebook";

        WebDriver browser = new HtmlUnitDriver() {
            @Override
            public WebElement findElement(By by) {
                try {
                    return super.findElement(by);
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("Can't find " + by + " in " + getPageSource());
                }
            }
        };

        browser.get(url);
        browser.findElement(By.linkText("Add contact")).click();

        browser.findElement(By.name("fullName")).sendKeys("Darth Vader");
        browser.findElement(By.name("phoneNumber")).sendKeys("666");
        browser.findElement(By.name("addContact")).click();

        browser.findElement(By.linkText("Find contact")).click();
        browser.findElement(By.name("nameQuery")).sendKeys("vader");
        browser.findElement(By.name("nameQuery")).submit();

        assertThat(browser.getPageSource())
            .contains("Darth Vader (666)");
    }

}
