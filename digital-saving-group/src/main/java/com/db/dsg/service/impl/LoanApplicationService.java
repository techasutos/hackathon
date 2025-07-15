package com.db.dsg.service.impl;

import com.db.dsg.dtos.LoanAuditLogDto;
import com.db.dsg.dtos.LoanDto;
import com.db.dsg.model.*;
import com.db.dsg.repository.LoanApplicationRepository;
import com.db.dsg.repository.LoanAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    private final LoanApplicationRepository loanRepo;
    private final LoanAuditLogRepository auditLogRepo;
    private final GroupFundService groupFundService;
    private final ModelMapper mapper;

    // Apply loan
    public LoanDto applyLoan(Member member, BigDecimal amount, String purpose) {
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setAmount(amount);
        loan.setRemainingBalance(amount);
        loan.setStatus(LoanStatus.PENDING);
        loan.setPurpose(purpose);
        loan.setApplicationDate(LocalDate.now());

        Loan saved = loanRepo.save(loan);

        auditLogRepo.save(new LoanAuditLog(null, saved, LoanStatus.PENDING,
                member.getName(), LocalDateTime.now(), "Loan applied by " + member.getName()));

        return toDto(saved);
    }

    public LoanDto approveLoan(Long loanId, MemberUser approver) {
        Loan loan = getLoanOrThrow(loanId);

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Only PENDING loans can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDate.now());

        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(new LoanAuditLog(null, updated, LoanStatus.APPROVED,
                approver.getUsername(), LocalDateTime.now(), "Approved by " + approver.getUsername()));

        return toDto(updated);
    }

    public LoanDto disburseLoan(Long loanId, MemberUser treasurer) {
        Loan loan = getLoanOrThrow(loanId);

        if (loan.getStatus() != LoanStatus.APPROVED)
            throw new IllegalStateException("Only APPROVED loans can be disbursed");

        if (!treasurer.hasRole("TREASURER"))
            throw new AccessDeniedException("Only treasurer can disburse loans");

        BigDecimal amount = loan.getAmount();
        Long groupId = loan.getMember().getGroup().getId();
        groupFundService.subtractFromFund(groupId, amount);

        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        loan.setRemainingBalance(amount);

        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(new LoanAuditLog(null, updated, LoanStatus.DISBURSED,
                treasurer.getUsername(), LocalDateTime.now(),
                "Disbursed ₹" + amount + " by " + treasurer.getUsername()));

        return toDto(updated);
    }

    public LoanDto rejectLoan(Long loanId, MemberUser user) {
        Loan loan = getLoanOrThrow(loanId);

        if (loan.getStatus() != LoanStatus.PENDING && loan.getStatus() != LoanStatus.APPROVED)
            throw new IllegalStateException("Only PENDING or APPROVED loans can be rejected");

        loan.setStatus(LoanStatus.REJECTED);
        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(new LoanAuditLog(null, updated, LoanStatus.REJECTED,
                user.getUsername(), LocalDateTime.now(), "Rejected by " + user.getUsername()));

        return toDto(updated);
    }

    public LoanDto repayAndUpdateFund(Long loanId, BigDecimal repaymentAmount, MemberUser memberUser) {
        Loan loan = getLoanOrThrow(loanId);
        Member member = memberUser.getMember();

        if (!loan.getMember().getId().equals(member.getId()))
            throw new AccessDeniedException("This loan does not belong to the current user.");

        if (loan.getStatus() != LoanStatus.DISBURSED && loan.getStatus() != LoanStatus.REPAID)
            throw new IllegalStateException("Only DISBURSED loans can be repaid.");

        BigDecimal newBalance = loan.getRemainingBalance().subtract(repaymentAmount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Repayment exceeds remaining loan amount.");

        loan.setRemainingBalance(newBalance);
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.REPAID);
            loan.setRepaymentDate(LocalDate.now());
        }

        Loan updatedLoan = loanRepo.save(loan);

        groupFundService.addToFund(loan.getMember().getGroup().getId(), repaymentAmount);

        auditLogRepo.save(new LoanAuditLog(null, updatedLoan,
                loan.getStatus(), memberUser.getUsername(), LocalDateTime.now(),
                "Repayment of ₹" + repaymentAmount + " by " + memberUser.getUsername()));

        return toDto(updatedLoan);
    }

    public List<LoanDto> getLoansByMember(Member member) {
        return loanRepo.findByMember(member).stream().map(this::toDto).toList();
    }

    public List<LoanDto> getLoansByGroupId(Long groupId) {
        return loanRepo.findByMember_Group_Id(groupId).stream().map(this::toDto).toList();
    }

    public List<LoanAuditLogDto> getAuditLogs(Long loanId) {
        return auditLogRepo.findByLoan_IdOrderByTimestampAsc(loanId)
                .stream()
                .map(this::toLogDto)
                .toList();
    }

    public List<LoanDto> getMonthlyRepayments(Long groupId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return loanRepo.findByMember_Group_IdAndRepaymentDateBetween(groupId, start, end)
                .stream().map(this::toDto).toList();
    }

    public List<LoanDto> getOverdueLoans(Long groupId) {
        LocalDate due = LocalDate.now().minusDays(90);
        return loanRepo.findByMember_Group_IdAndStatusAndDisbursementDateBefore(
                        groupId, LoanStatus.DISBURSED, due)
                .stream().map(this::toDto).toList();
    }

    public List<Member> getMembersWithOverdueLoans(Long groupId) {
        return getOverdueLoans(groupId).stream()
                .map(dto -> loanRepo.findById(dto.getId()).orElse(null)) // Convert DTO back to entity
                .filter(Objects::nonNull)
                .map(Loan::getMember)
                .distinct()
                .toList();
    }

    // --- Helper Methods ---
    private Loan getLoanOrThrow(Long id) {
        return loanRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    }

    private LoanDto toDto(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .remainingBalance(loan.getRemainingBalance())
                .status(loan.getStatus().name())
                .purpose(loan.getPurpose())
                .applicationDate(loan.getApplicationDate())
                .approvalDate(loan.getApprovalDate())
                .disbursementDate(loan.getDisbursementDate())
                .repaymentDate(loan.getRepaymentDate())
                .memberId(loan.getMember().getId())
                .memberName(loan.getMember().getName())
                .build();
    }

    private LoanAuditLogDto toLogDto(LoanAuditLog log) {
        return LoanAuditLogDto.builder()
                .id(log.getId())
                .performedBy(log.getPerformedBy())
                .description(log.getDescription())
                .status(log.getStatus().name())
                .timestamp(log.getTimestamp())
                .build();
    }
}