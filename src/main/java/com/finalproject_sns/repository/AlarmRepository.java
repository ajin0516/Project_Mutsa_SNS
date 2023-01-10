package com.finalproject_sns.repository;

import com.finalproject_sns.domain.Alarm;
import com.finalproject_sns.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {

    Alarm findByUserAndTargetId(User user, Long targetId);

    Page<Alarm> findByUserAndDeletedAtNull(User user, Pageable pageable);

    @Modifying
    @Query("update Alarm a set a.deletedAt = null where a.id = :alarmId")
    void reSave(@Param("alarmId") Long alarmId);
}
