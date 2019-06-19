package pl.codeve.inspectorbudget.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.codeve.inspectorbudget.user.avatar.Avatar;
import pl.codeve.inspectorbudget.user.avatar.AvatarService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ScheduledTasks {

    private AvatarService avatarService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    ScheduledTasks(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Scheduled(fixedRate = 86400000) //24h
    public void sweepOutUnusedAvatars() {

        logger.info("Scheduled task: removing unused avatars started. Execution time {}", dateTimeFormatter.format(LocalDateTime.now()));

        List<Avatar> unusedAvatars = avatarService.findAllByInUse(false);
        int counter = 0;
        for (Avatar unusedAvatar : unusedAvatars) {
            if (unusedAvatar.getCreatedAt().isBefore(LocalDateTime.now()
                    .minus(1, ChronoUnit.HOURS))) {
                avatarService.delete(unusedAvatar);
                counter++;
            }
        }
        logger.info("Scheduled task: removing unused avatars completed. Removed {} avatars. Completion time {}", counter, dateTimeFormatter.format(LocalDateTime.now()));
    }
}
