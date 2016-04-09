package com.lodenrogue.fast.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "product_backlog_items")
public class ProductBacklogItem extends ResourceSupport {
	@Id
	@GeneratedValue
	@Column(name = "id")
	@JsonProperty(value = "id")
	private long entityId;

	@Column(name = "story")
	private String story;

	@Column(name = "author")
	private long authorId;

	@Column(name = "date_created")
	private Calendar dateCreated;

	@Column(name = "priority")
	private long priority;

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}

	public Calendar getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Calendar dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

}
