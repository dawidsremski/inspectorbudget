package pl.codeve.inspectorbudget.user.avatar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    List<Avatar> findAllByInUse(boolean inUse);
}
