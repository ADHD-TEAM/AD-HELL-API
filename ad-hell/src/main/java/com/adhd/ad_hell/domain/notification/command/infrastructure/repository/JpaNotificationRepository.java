    package com.adhd.ad_hell.domain.notification.command.infrastructure.repository;

    import com.adhd.ad_hell.domain.notification.command.domain.aggregate.Notification;
    import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {

        Page<Notification> findByUserId(Long userId, Pageable pageable);

        long countByUserIdAndReadYn(Long userId, YnType readYn);
    }