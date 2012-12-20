package com.exilesoft.bareknuckleweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eaxy.Element;
import org.eaxy.html.Xhtml;
import org.eaxy.html.XhtmlFactory;

public class ContactServlet extends HttpServlet {
    private ContactStorage contactStorage;
    private TransactionManager transactionManager;
    private final XhtmlFactory xhtmlFactory = new XhtmlFactory(this);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try (Transaction tx = transactionManager.begin()) {
            resp.setContentType("text/html");

            PrintWriter writer = resp.getWriter();
            if (req.getPathInfo().equals("/create.html")) {
                showCreatePage(writer);
            } else {
                showFindPage(writer, req.getParameter("nameQuery"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showFindPage(PrintWriter writer, String nameQuery) throws SQLException, IOException {
        Xhtml document = xhtmlFactory.create("html/contact/index.html");

        document.getForm("#findForm").set("nameQuery", nameQuery);

        Element contactsEl = document.select("#contacts");
        Element contactTmpl = contactsEl.take(".contact");
        for (Contact contact : contactStorage.find(nameQuery)) {
            contactsEl.add(contactTmpl.copy().text(contact.print()));
        }

        document.write(writer);
    }

    private void showCreatePage(PrintWriter writer) throws IOException {
        xhtmlFactory.create("html/contact/create.html").write(writer);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try (Transaction tx = transactionManager.begin()) {
            contactStorage.createContact(req.getParameter("fullName"), req.getParameter("phoneNumber"));
            tx.setCommit();
            resp.sendRedirect(req.getContextPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws ServletException {
        setContactStorage(new JdbcContactStorage());
        setTransactionManager(new JdbcTransactionManager());
    }

    public void setContactStorage(ContactStorage contactStorage) {
        this.contactStorage = contactStorage;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
