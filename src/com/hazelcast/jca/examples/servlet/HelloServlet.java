package com.hazelcast.jca.examples.servlet;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/Hello")
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = -8314035702649252239L;

    @Inject
    protected QueueService queueService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        PrintWriter out = resp.getWriter();
        out.write("<h1>Hazelcast JCA Example</h1>");
        out.write("<form action='?' method='GET'><input name='action' value='put' type='hidden' />"
                + "<input type='text' name='data' /><input type='submit' value='PUT' /></form>");
//        out.write("<a href='?action=clear'>CLEAR</a>");
        out.write("<br />");
        out.write("<br />");

        try {
            String data = req.getParameter("data");
            String message = queueService.stage1("q", data);
            out.write("Message is offered and polled in different transactions" + message);
            out.write("<br/>");
        } finally {
            closeWriter(out);
        }
    }

    private void closeWriter(PrintWriter out) {
        if (out != null) {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
