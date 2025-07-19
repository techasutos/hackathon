package com.db.dsg.repository;

import com.db.dsg.model.LoanRepayment;
import com.db.dsg.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {
    List<LoanRepayment> findByLoanId(Long loanId);

    List<LoanRepayment> findByLoan_Member(Member member);

    List<LoanRepayment> findByLoan_Member_Group_Id(Long groupId);

    List<LoanRepayment> findByLoan_Member_Id(Long memberId);

    List<LoanRepayment> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT r FROM LoanRepayment r WHERE r.date BETWEEN :from AND :to AND r.loan.member.group.id = :groupId")
    List<LoanRepayment> findByDateBetweenAndLoanGroupId(LocalDate from, LocalDate to, Long groupId);
}