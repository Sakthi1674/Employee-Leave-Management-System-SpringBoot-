package com.example.EmployeeLeaveManagement;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Elacontroller {
    String jdbcurl = "jdbc:mysql://localhost:3306/mydatabase";

    @GetMapping("/Home")
    public String home() {
        return "Home";
    }

    @GetMapping("/Login")
    public String login() {
        return "Login";
    }

    @GetMapping("/Signup")
    public String signup() {
        return "Signup";
    }

    @PostMapping("/loginforDashboard")
    public String loginfordash(@RequestParam("username") String name, @RequestParam("password") String password) {
        System.out.print(name + " " + password);
        return "Dashboard";
    }

    @PostMapping("/Signup")
    public String signup(@RequestParam("username") String username, @RequestParam("userId") String userId, @RequestParam("password") String password, @RequestParam("address") String address) {
        System.out.println("Inside signup method");
        System.out.println("The attributes are " + username + " " + userId + " " + password + " " + address);
        try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "root")) {
            String sql = "INSERT INTO user VALUES (?, ?, ?, ?)";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1, username);
            pstatement.setString(2, userId);
            pstatement.setString(3, password);
            pstatement.setString(4, address);
            pstatement.execute();
            System.out.println("Database updated successfully");
        } catch (Exception e) {
            System.out.println("The exception occurred is " + e);
        }
        return "Login";
    }

    @GetMapping("/LeaveForm")
    public String leaveform() {
        return "LeaveForm";
    }

    @PostMapping("/LeaveForm")
    public String lform(@RequestParam("employee_id") int EID, @RequestParam("employee_name") String E_NAME, @RequestParam("reason") String reason, @RequestParam("start_date") Date start_date, @RequestParam("end_date") Date end_date, Model model) {
        System.out.println("Inside leave form method");
        System.out.println("The attributes are " + EID + " " + E_NAME + " " + reason + " " + start_date + " " + end_date);
        try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "root")) {
            String sql = "INSERT INTO leaves (EID, E_NAME, reason, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setInt(1, EID);
            pstatement.setString(2, E_NAME);
            pstatement.setString(3, reason);
            pstatement.setDate(4, start_date);
            pstatement.setDate(5, end_date);
            pstatement.execute();
            System.out.println("Database updated successfully");
        } catch (Exception e) {
            System.out.println("The exception occurred is " + e);
        }
        return "Login";
    }

    @PostMapping("/Adminlogin")
    public String Datas(@RequestParam(name = "viewType", defaultValue = "table") String viewType, Model model) {
        List<Map<String, Object>> data = fetchLeave();
        model.addAttribute("dataList", data);
        model.addAttribute("viewType", viewType);
        return "Admin";
    }

    @ModelAttribute("dataList")
    public List<Map<String, Object>> fetchLeave() {
        List<Map<String, Object>> data = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "root")) {
            String sql = "SELECT * FROM leaves";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Map<String, Object> mp = new HashMap<>();
                mp.put("EID", rs.getInt("EID"));
                mp.put("E_NAME", rs.getString("E_NAME"));
                mp.put("reason", rs.getString("reason"));
                mp.put("start_date", rs.getDate("start_date"));
                mp.put("end_date", rs.getDate("end_date"));
                data.add(mp);
            }
            System.out.println("The map data are " + data);
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return data;
    }

    // Accept leave request
    @PostMapping("/acceptLeave")
    public String acceptLeave(@RequestParam("EID") int EID, Model model) {
        try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "root")) {
            String sql = "UPDATE leaves SET status = 'Accepted' WHERE EID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, EID);
            statement.executeUpdate();
            System.out.println("Leave request accepted");
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return "redirect:/Adminlogin"; // Redirect to refresh the admin page
    }

    // Decline leave request
    @PostMapping("/declineLeave")
    public String declineLeave(@RequestParam("EID") int EID, Model model) {
        try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "root")) {
            String sql = "DELETE FROM leaves WHERE EID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, EID);
            statement.executeUpdate();
            System.out.println("Leave request declined");
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return "redirect:/Adminlogin"; // Redirect to refresh the admin page
    }
}
