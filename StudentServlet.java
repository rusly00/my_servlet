package com.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class StudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String action = request.getParameter("action");

        String jdbcURL = "jdbc:mysql://localhost:3306/student";
        String dbUser = "root";
        String dbPassword = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {

                switch (action) {
                    case "Insert":
                        PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO studentmgt(name, email) VALUES (?, ?)");
                        insertStmt.setString(1, name);
                        insertStmt.setString(2, email);
                        insertStmt.executeUpdate();
                        out.println("<p>✅ Student added successfully.</p>");
                        break;

                    case "View":
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM studentmgt");
                        out.println("<h2>Student List</h2>");
                        out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>Email</th></tr>");
                        while (rs.next()) {
                            out.println("<tr><td>" + rs.getInt("id") + "</td>");
                            out.println("<td>" + rs.getString("name") + "</td>");
                            out.println("<td>" + rs.getString("email") + "</td></tr>");
                        }
                        out.println("</table>");
                        break;

                    case "Update":
                        PreparedStatement updateStmt = conn.prepareStatement("UPDATE studentmgt SET email=? WHERE name=?");
                        updateStmt.setString(1, email);
                        updateStmt.setString(2, name);
                        int updated = updateStmt.executeUpdate();
                        out.println(updated > 0 ? "<p>✅ Updated successfully.</p>" : "<p>⚠️ Student not found.</p>");
                        break;

                    case "Delete":
                        PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM studentmgt WHERE name=?");
                        deleteStmt.setString(1, name);
                        int deleted = deleteStmt.executeUpdate();
                        out.println(deleted > 0 ? "<p>🗑️ Deleted successfully.</p>" : "<p>⚠️ Student not found.</p>");
                        break;

                    default:
                        out.println("<p>⚠️ Invalid action.</p>");
                }
            }
        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}
