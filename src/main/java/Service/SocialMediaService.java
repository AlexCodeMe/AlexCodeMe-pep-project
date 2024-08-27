package Service;

import java.util.List;

import DAO.SocialMediaDAO;
import Model.Account;
import Model.Message;

public class SocialMediaService {

    public SocialMediaDAO socialMediaDAO;

    public SocialMediaService() {
        socialMediaDAO = new SocialMediaDAO();
    }

    public SocialMediaService(SocialMediaDAO socialMediaDao) {
        this.socialMediaDAO = socialMediaDao;
    }

    public Account register(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.length() < 4) {
            return null; // invalid credentials
        }
        if (socialMediaDAO.getAccountByUsername(username) != null) {
            return null; // username already exists
        }

        Account account = new Account(username, password);
        return socialMediaDAO.insertAccount(account);
    }

    public Account login(String username, String password) {
        Account account = socialMediaDAO.getAccountByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            return account;
        }

        return null;
    }

    public Message createMessage(Message message) {
        Account account = socialMediaDAO.getAccountById(message.getPosted_by());
        if (account == null) {
            return null;
        }
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            return null;
        }

        return socialMediaDAO.postMessage(message);
    }

    public List<Message> getAllMessages() {
        return socialMediaDAO.getAllMessages();
    }

    public Message getMessageById(int id) {
        return socialMediaDAO.getMessageById(id);
    }

    public List<Message> getMessagesByAccountId(int id) {
        return socialMediaDAO.getMessagesByAuthorId(id);
    }

    public Message deleteMessageById(int id) {
        return socialMediaDAO.deleteMessageById(id);
    }

    public Message updateMessage(int id, Message message) {
        if (message.getMessage_text().isEmpty()) {
            return null;
        }
        return socialMediaDAO.updateMessageById(id, message);
    }

}
