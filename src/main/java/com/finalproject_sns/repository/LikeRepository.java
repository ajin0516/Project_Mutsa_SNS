package com.finalproject_sns.repository;

import com.finalproject_sns.domain.Like;
import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user,Post post);


//    @Query("SELECT COUNT(*) - COUNT(deletedAt) FROM Like WHERE id= :post_id")
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post= :post AND l.deletedAt is null")
//    @Query("SELECT COUNT(deletedAt) FROM Like WHERE deletedAt IS NULL")
    Integer countByPost(@Param("post") Post post);

    @Transactional
    @Modifying
    @Query("UPDATE Like l SET l.deletedAt=null WHERE l.id= :likeId")
    void reSave(@Param("likeId") Long likeId);


}
