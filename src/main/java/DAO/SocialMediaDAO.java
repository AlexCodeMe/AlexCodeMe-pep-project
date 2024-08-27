package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

public class SocialMediaDAO {

    public Account insertAccount(Account account) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generated_account_id = (int) rs.getInt(1);
                return new Account(generated_account_id, account.getUsername(), account.getPassword());
            }
        } catch (SQLException e) {
            System.out.println("SocialMediaDAO.insertAccount(): " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Account getAccountByUsername(String username) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("SocialMediaDAO.getMessageById " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public Account getAccountById(int id) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
            }
        } catch (SQLException e) {
            System.out.println("SocialMediaDAO.getMessageById " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getAllMessages() {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> msgs = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Message msg = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));

                msgs.add(msg);
            }
        } catch (SQLException e) {
            System.out.println("SocialMediaDAO.getAllMessages(): " + e.getMessage());
            e.printStackTrace();
        }

        return msgs;
    }

    public Message getMessageById(int id) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println("SocialMediaDAO.getMessageById " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Message postMessage(Message message) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedMessageId = generatedKeys.getInt(1);
                    return new Message(generatedMessageId, message.getPosted_by(), message.getMessage_text(),
                            message.getTime_posted_epoch());
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            System.out.println("SocialMediaDAO.postMessage " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Message deleteMessageById(int id) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            Message msg = getMessageById(id);
            if (msg == null) {
                return null; // msg not found
            }

            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return msg;
            }
        } catch (Exception e) {
            System.out.println("SocialMediaDAO.deleteMessageById " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Message updateMessageById(int id, Message message) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            Message msg = getMessageById(id);
            if (msg == null) {
                return null; // msg not found
            }
            
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, message.getMessage_text());
            ps.setInt(2, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return getMessageById(id);
            }
        } catch (Exception e) {
            System.out.println("SocialMediaDAO.updateMessageById " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Message> getMessagesByAuthorId(int id) {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> msgs = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message msg = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                    );
                msgs.add(msg);
            }
        } catch (Exception e) {
            System.out.println("SocialMediaDAO.getMessagesByAuthorId " + e.getMessage());
            e.printStackTrace();
        }
        return msgs;
    }
}
