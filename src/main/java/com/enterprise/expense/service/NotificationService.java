package com.enterprise.expense.service;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;
    public void send(User user, String title, String message, String type) {
        repo.save(Notification.builder().user(user).title(title).message(message).type(type).build());
    }
    public List<Notification> getMyNotifications(User user) { return repo.findByUserOrderByCreatedAtDesc(user); }
    public long countUnread(User user) { return repo.countUnread(user); }
    public void markAllRead(User user) {
        List<Notification> list = repo.findByUserOrderByCreatedAtDesc(user);
        list.forEach(n -> n.setRead(true));
        repo.saveAll(list);
    }
}
