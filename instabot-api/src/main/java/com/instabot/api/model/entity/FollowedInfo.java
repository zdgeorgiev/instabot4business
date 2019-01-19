package com.instabot.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "followedInfo")
public class FollowedInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonIgnore
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "date_followed")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "EET")
	private LocalDateTime dateFollowed;

	@Column(name = "followStatus")
	@Enumerated(EnumType.STRING)
	private FollowStatus followStatus;

	public enum FollowStatus {
		FOLLOWING,
		NOT_FOLLOWING
	}

	public FollowedInfo() {
		this.dateFollowed = LocalDateTime.now();
	}

	public FollowedInfo(String username, FollowStatus followStatus) {
		this();
		this.username = username;
		this.followStatus = followStatus;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getDateFollowed() {
		return dateFollowed;
	}

	public void setDateFollowed(LocalDateTime dateFollowed) {
		this.dateFollowed = dateFollowed;
	}

	public FollowStatus getFollowStatus() {
		return followStatus;
	}

	public void setFollowStatus(FollowStatus followStatus) {
		this.followStatus = followStatus;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FollowedInfo))
			return false;

		FollowedInfo that = (FollowedInfo) o;

		return username.equals(that.username);
	}

	@Override public int hashCode() {
		return username.hashCode();
	}
}
