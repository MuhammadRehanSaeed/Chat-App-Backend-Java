package com.rehancode.chatapp.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rehancode.chatapp.Entity.Chat;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

}
