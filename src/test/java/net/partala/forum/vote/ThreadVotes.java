package net.partala.forum.vote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Fake entity for Hibernate
//BaseIntegrationTest can't truncate tables without it
@Entity
@Table(name = "thread_votes")
public class ThreadVotes {

    @Id
    @Column(name = "user_id")
    private Long userId;

    public ThreadVotes() {}
}
