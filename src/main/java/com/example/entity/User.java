package com.example.entity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Principal, UserDetails {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String login;

	@Column
	private String password;

	//	@Enumerated(EnumType.STRING)
	//	@Column(name = "role")
	//	private Role role;

	@Override
	public String getName() {
		return login;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

		//		if (id != null || Role.user != role) {
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		//		}
		//		if (role != null) {
		//			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
		//		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
