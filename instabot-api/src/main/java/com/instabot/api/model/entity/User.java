package com.instabot.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonIgnore
	private Long id;

	@Column(name = "username")
	private String username;

	@JsonIgnore
	@Column(name = "first_login_date")
	private LocalDateTime dateCreated;

	@Column(name = "hashtags")
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> hashtags;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "followed_id")
	private Set<FollowedInfo> everFollowed;

	@Column(name = "toFollow")
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> toFollow;

	@Column(name = "toLike")
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> toLike;

	public User() {
		dateCreated = LocalDateTime.now();
		hashtags = new HashSet<>();
		everFollowed = new HashSet<>();
		toFollow = new HashSet<>();
		toLike = new HashSet<>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Set<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(Set<String> hashtags) {
		this.hashtags = hashtags;
	}

	public Set<FollowedInfo> getEverFollowed() {
		return everFollowed;
	}

	public void setEverFollowed(Set<FollowedInfo> everFollowed) {
		this.everFollowed = everFollowed;
	}

	public Set<String> getToFollow() {
		return toFollow;
	}

	public void setToFollow(Set<String> toFollow) {
		this.toFollow = toFollow;
	}

	public Set<String> getToLike() {
		return toLike;
	}

	public void setToLike(Set<String> toLike) {
		this.toLike = toLike;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User))
			return false;

		User user = (User) o;

		return username.equals(user.username);
	}

	@Override public int hashCode() {
		return username.hashCode();
	}
}
