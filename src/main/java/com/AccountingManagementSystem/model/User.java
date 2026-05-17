package com.AccountingManagementSystem.model;

public class User {
	private int id;
	private String fullName;
	private String username;
	private String email;
	private String password;
	private int deletedRecordId;
	private String deletedAt;

	public User() {
	}

	public User(String fullName, String username, String email, String password) {
		this.fullName = fullName;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDeletedRecordId() {
		return deletedRecordId;
	}

	public void setDeletedRecordId(int deletedRecordId) {
		this.deletedRecordId = deletedRecordId;
	}

	public String getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(String deletedAt) {
		this.deletedAt = deletedAt;
	}

	// These methods keep older code working if it still calls getName or setName.
	public String getName() {
		return fullName;
	}

	public void setName(String name) {
		this.fullName = name;
	}
}
