package cl.exercise.users.repository;

import cl.exercise.users.model.UserLogModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserLogRepository extends JpaRepository<UserLogModel, UUID> {

    Page<UserLogModel> findByUserModel_Id(UUID userId, Pageable pageable);


}
