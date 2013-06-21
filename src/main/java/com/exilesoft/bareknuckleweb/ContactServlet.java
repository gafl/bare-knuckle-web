package com.exilesoft.bareknuckleweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContactServlet extends HttpServlet {
    private ContactStorage contactStorage;
    private TransactionManager transactionManager;

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

    private void showFindPage(PrintWriter writer, String nameQuery) throws SQLException {
        writer.append("<html>");
        writer
	        .append("<form method='get'>")
	        .append("<input type='text' name='nameQuery' value='" + (nameQuery != null ? nameQuery : "") + "' />")
	        .append("<input type='submit' name='findContact' value='Find contact' />")
	        .append("</form>")
        ;

        writer.append("<ul id='contacts'>");
        for (Contact contact : contactStorage.find(nameQuery)) {
            writer.append("<li class='contact'>" + contact.print() + "</li>");
        }
        writer.append("</ul>");
        writer.append("</html>");
    }

    private void showCreatePage(PrintWriter writer) {
        writer.append("<html>");
        writer
            .append("<form method='post'>")
            .append("<li>Full name: <input type='text' name='fullName' /></li>")
            .append("<li>Phone number: <input type='text' name='phoneNumber' /></li>")
            .append("<li><input type='submit' name='addContact' value='Add contact' /></li>")
            .append("</form>")
            ;
        writer.append("</html>");
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
