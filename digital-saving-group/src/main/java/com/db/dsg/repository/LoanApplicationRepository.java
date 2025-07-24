package com.db.dsg.repository;

import com.db.dsg.model.Loan;
import com.db.dsg.model.LoanStatus;
import com.db.dsg.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMember_Group_Id(Long groupId);

    List<Loan> findByMember(Member member);

    @Query("SELECT l FROM Loan l JOIN FETCH l.member WHERE l.id = :id")
    Optional<Loan> findByIdWithMember(@Param("id") Long id);

    List<Loan> findByMember_Group_IdAndRepaymentDateBetween(Long groupId, LocalDate start, LocalDate end);

    List<Loan> findByMember_Group_IdAndStatusAndDisbursementDateBefore(Long groupId, LoanStatus status, LocalDate date);

    @Query("SELECT l FROM Loan l JOIN FETCH l.member m JOIN FETCH m.group WHERE l.id = :id")
    Optional<Loan> findByIdWithMemberAndGroup(Long id);
}