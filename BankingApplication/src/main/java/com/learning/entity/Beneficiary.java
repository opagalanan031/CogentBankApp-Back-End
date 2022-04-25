package com.learning.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.learning.enums.AccountType;
import com.learning.enums.ActiveStatus;
import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Beneficiary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer beneficiaryId;
	private Integer accountNumber;
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	@Enumerated(EnumType.STRING)
	private ApprovedStatus approvedStatus;
	@Enumerated(EnumType.STRING)
	private ActiveStatus activeStatus;
	
	@NotNull
	private Date beneficiaryAddedDate;
	
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonManagedReference
	@JsonIgnore
	private User mainUser;
}
