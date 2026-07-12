package net.partala.forum.vote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Fake entity for Hibernate
//BaseIntegrationTest can't truncate tables without it
@Entity
@Table(name = "comment_votes")
public class CommentVotes {

    @Id
    @Column(name = "user_id")
    private Long userId;

    public CommentVotes() {}
}
