package com.learning.payload.request.staff;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
@Validated
public class ApproveAccountRequest {
	@NotNull
	private Integer accountNumber;
	@NotBlank
	private String staffUsername;
	@NotNull
	@Enumerated(EnumType.STRING)
	private ApprovedStatus approved;
}
