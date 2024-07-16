package com.main.vlbi.models.planning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.main.vlbi.models.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "UserInfo")
@Table(name = "UserInfo")
@Getter @Setter @NoArgsConstructor
public class UserInfo {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserInfoId")
 	@Setter(value = AccessLevel.NONE)
    private Long userInfoId;
	
	@Column(name = "Address1")
	private String address1;
	@Column(name = "Address2")
	private String address2;
	@Column(name = "Address3")
	private String address3;
	@Column(name = "Phone")
	private String phone;
	@Column(name = "Obsphone")
	private String obsphone;
	@Column(name = "Fax")
	private String fax;
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
	
	public UserInfo(User user)
	{
		setUser(user);
	}
	
	
	public UserInfo(User user, String address1, String phone)
	{
		setUser(user);
		setAddress1(address1);
		setPhone(phone);
	}


	public UserInfo(User user, String address1, String address2, String address3, String phone, String obsphone, String fax) {
		setAddress1(address1);
		setAddress2(address2);
		setAddress3(address3);
		setPhone(phone);
		setObsphone(obsphone);
		setFax(fax);
	}
	
	
	
}

