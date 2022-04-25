package com.learning.payload.response.customer;

import java.util.Date;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class AddAccountResponse {
	private AccountType accountType;
	private Double accountBalance;
	private ApprovedStatus approvedStatus;
	private Integer accountNumber;
	private Date dateCreated;
	private Integer customerId;
	
}
