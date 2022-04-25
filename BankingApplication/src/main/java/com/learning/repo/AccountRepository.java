package com.learning.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learning.entity.Account;
import com.learning.entity.User;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	public Optional<AccountType> findByAccountType(AccountType accountType);
	public List<Account> findAllByApprovedStatus(ApprovedStatus approvedStatus);
}
