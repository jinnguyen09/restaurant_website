package restaurant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.entity.Notification;
import restaurant.entity.User;
import restaurant.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderByCreateAtDesc(user);
    }

    public long countUnread(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findAllUnreadByUser(user);
        if (!unread.isEmpty()) {
            unread.forEach(n -> n.setRead(true));
            notificationRepository.saveAll(unread);
        }
    }

    public Page<Notification> getNotificationsForUser(User user, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return notificationRepository.findByUserOrderByCreateAtDesc(user, pageable);
    }

    public void createNotification(User user, String title, String content, String status, String url) {
        Notification noti = new Notification();
        noti.setUser(user);
        noti.setTitle(title);
        noti.setContent(content);
        noti.setStatus(status);
        noti.setLinkUrl(url);
        notificationRepository.save(noti);
    }
}