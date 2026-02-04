package com.java.k8s.nobatch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private int point;

	@Column(unique = true)
	private String password;

	private Member(String name, int point, String password){
		this.name = name;
		this.point = point;
		this.password = password;
	}

	public Member() {

	}

	public void updatePoint(int point){
		this.point = this.point+point;
	}

	public static class Builder{
		private String name;
		private int point;
		private String password;
		public Builder name(String name){this.name = name; return this;}
		public Builder point(int point){this.point = point; return this;}
		public Builder password(String password){this.password = password; return this;}

		public Member build(){
			return new Member(this.name, this.point,this.password);
		}
	}

}
