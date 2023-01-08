package com.finalproject_sns.repository;

import com.finalproject_sns.domain.Alarm;
import com.finalproject_sns.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {

    Alarm findByUserAndTargetId(User user, Long targetId);

//    Alarm findByUser(User user);


//    @Query(value = "SELECT * FROM alarm l where l.deleted_at = null and l.id= :userId order by l.create_at",nativeQuery = true)

//    Page<Alarm> findByUserId(@Param("userId") Long userId,Pageable pageable);
//    Page<Alarm> findByDeletedAtNull(Pageable pageable);


    Page<Alarm> findByUserIdAndDeletedAtNull(Long userId, Pageable pageable);

//    Page<Alarm> findByUserNameAndDeletedAtNull(String userName, Pageable pageable);


//    @Query(value = "SELECT * FROM alarm l where l.deleted_at = null order by l.create_at DESC", nativeQuery = true)
//    @Query(value = "SELECT l FROM Alarm l where l.deletedAt is NULL order by l.createAt DESC",nativeQuery = true)
//    Page<Alarm> findAll(Pageable pageable, Long userId);


    @Modifying
    @Query("update Alarm a set a.deletedAt = null where a.id = :alarmId")
    void reSave(@Param("alarmId") Long alarmId);
}
