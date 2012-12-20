package com.exilesoft.bareknuckleweb;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eaxy.Xml;
import org.eaxy.html.HtmlForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class ContactServletTest {

    private final HttpServletRequest req = mock(HttpServletRequest.class);
    private final HttpServletResponse resp = mock(HttpServletResponse.class);
    private final ContactServlet servlet = new ContactServlet();
    private final ContactStorage storage = mock(ContactStorage.class);
    private final StringWriter html = new StringWriter();

    @Test
    public void shouldShowCreationForm() throws Exception {
        when(req.getPathInfo()).thenReturn("/create.html");
        servlet.doGet(req, resp);

        verify(resp).setContentType("text/html");

        HtmlForm htmlForm = getHtmlForm();
		assertThat(htmlForm.getSubmitButton().val()).isEqualTo("Add contact");
        assertThat(htmlForm.getFieldNames()).containsExactly("fullName", "phoneNumber", "addContact");
    }

	@Test
    public void shouldAddSaveContact() throws Exception {
        String webAppRoot = "/thiscontactweb";

        when(req.getContextPath()).thenReturn(webAppRoot);
        when(req.getParameter("fullName")).thenReturn("Darth");
        when(req.getParameter("phoneNumber")).thenReturn("666");
        servlet.doPost(req, resp);

        verify(storage).createContact("Darth", "666");

        verify(resp).sendRedirect(webAppRoot);
    }

    @Test
    public void shouldFindContact() throws Exception {
        when(req.getPathInfo()).thenReturn("/index.html");

        when(req.getParameter("nameQuery")).thenReturn("Darth");

        List<Contact> contact = Arrays.asList(new Contact("Darth Vader", "666"),
                new Contact("Darth Sidious", "666"));
        when(storage.find(Matchers.anyString())).thenReturn(contact);


        servlet.doGet(req, resp);

        assertThat(getHtmlForm().getFieldNames())
        	.containsOnly("nameQuery", "findContact");
        assertThat(getHtmlForm().getSubmitButton().val()).isEqualTo("Find contact");

        assertThat(Xml.xml(html).find("...", "#contacts", ".contact").check().texts())
        	.containsExactly("Darth Vader (666)", "Darth Sidious (666)");

        verify(storage).find("Darth");
    }

    private HtmlForm getHtmlForm() {
		return new HtmlForm(Xml.xml(html).select("form"));
	}

	@Before
    public void setupServlet() throws IOException, SQLException {
        servlet.setContactStorage(storage);
        TransactionManager txManager = mock(TransactionManager.class);
        servlet.setTransactionManager(txManager);
        when(txManager.begin()).thenReturn(mock(Transaction.class));
        when(resp.getWriter()).thenReturn(new PrintWriter(html));
    }

}
