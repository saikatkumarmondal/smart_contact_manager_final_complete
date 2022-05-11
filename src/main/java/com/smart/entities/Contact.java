package com.smart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "contact_tables")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	@NotBlank
	@Size(min = 3,max = 12,message = "name must be 3 to 12 characters")
	private String name;
	@NotBlank
	@Size(min = 3,max = 12,message = "nickName must be 3 to 12 characters")
	private String nickName;
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
	private String email;
	@NotBlank
	@Size(min = 2,max = 12,message = "phone must be 2 to 13 numbers")
	private String phone;
	@NotBlank
	@Size(min = 3,max = 12,message = "work must be 3 to 12 characters")
	private String work;
	@Column(length = 2000)
	private String about;
	private String image;
	@ManyToOne
	@JsonIgnore
	private User user;
	public int getcId() {
		return cId;
	}
	public void setcId(int cId) {
		this.cId = cId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Contact(int cId, String name, String nickName, String email, String phone, String work, String about,
			String image, User user) {
		super();
		this.cId = cId;
		this.name = name;
		this.nickName = nickName;
		this.email = email;
		this.phone = phone;
		this.work = work;
		this.about = about;
		this.image = image;
		this.user = user;
	}
	public Contact() {
		
	}
	@Override
	public boolean equals(Object obj) {
		return this.cId==((Contact)obj).getcId();
	}

	
}
