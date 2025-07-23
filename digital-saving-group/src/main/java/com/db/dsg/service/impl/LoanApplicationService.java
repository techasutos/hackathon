package com.db.dsg.service.impl;

import com.db.dsg.dtos.LoanAuditLogDto;
import com.db.dsg.dtos.LoanDto;
import com.db.dsg.dtos.LoanRequestDto;
import com.db.dsg.model.*;
import com.db.dsg.repository.LoanApplicationRepository;
import com.db.dsg.repository.LoanAuditLogRepository;
import jakarta.transaction.Transactional;
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

    public LoanDto applyLoan(Member member, LoanRequestDto request) {
        Loan loan = Loan.builder()
                .member(member)
                .amount(request.getAmount())
                .remainingBalance(request.getAmount())
                .status(LoanStatus.PENDING)
                .purpose(request.getPurpose())
                .purposeDescription(request.getPurposeDescription())
                .applicationDate(LocalDate.now())
                .createdDate(LocalDate.now())
                .tenureMonths(request.getTenureMonths())
                .monthlyEmi(request.getMonthlyEmi())
                .monthlyIncome(request.getMonthlyIncome())
                .build();

        Loan saved = loanRepo.save(loan);

        auditLogRepo.save(
                LoanAuditLog.builder()
                        .loan(saved)
                        .status(LoanStatus.PENDING)
                        .performedBy(member.getName())
                        .timestamp(LocalDateTime.now())
                        .description("Loan applied by " + member.getName())
                        .build()
        );

        return toDto(saved);
    }

    @Transactional
    public LoanDto approveLoan(Long loanId, MemberUser approver) {
        Loan loan = getLoanOrThrow(loanId);

        if (loan.getStatus() != LoanStatus.PENDING)
            throw new IllegalStateException("Only PENDING loans can be approved");

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDate.now());

        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(
                LoanAuditLog.builder()
                        .loan(updated)
                        .status(LoanStatus.APPROVED)
                        .performedBy(approver.getUsername())
                        .timestamp(LocalDateTime.now())
                        .description("Approved by " + approver.getUsername())
                        .build()
        );

        return toDto(updated);
    }

    /**
     * ✅ New: Member requests disbursement after loan is approved.
     */
    @Transactional
    public LoanDto requestDisbursement(Long loanId, MemberUser memberUser) {
        Loan loan = getLoanOrThrow(loanId);

        if (!loan.getMember().getId().equals(memberUser.getMember().getId()))
            throw new AccessDeniedException("You can only request disbursement for your own loans");

        if (loan.getStatus() != LoanStatus.APPROVED)
            throw new IllegalStateException("Only APPROVED loans can be requested for disbursement");

        loan.setStatus(LoanStatus.DISBURSE_REQUESTED);
        loan.setDisbursementRequestDate(LocalDate.now());

        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(
                LoanAuditLog.builder()
                        .loan(updated)
                        .status(LoanStatus.DISBURSE_REQUESTED)
                        .performedBy(memberUser.getUsername())
                        .timestamp(LocalDateTime.now())
                        .description("Disbursement requested by " + memberUser.getUsername())
                        .build()
        );

        return toDto(updated);
    }

    @Transactional
    public LoanDto disburseLoan(Long loanId, MemberUser treasurer) {
        Loan loan = getLoanOrThrow(loanId);

        if (loan.getStatus() != LoanStatus.DISBURSE_REQUESTED)
            throw new IllegalStateException("Loan must be DISBURSE_REQUESTED to disburse");

        if (!treasurer.hasRole("TREASURER"))
            throw new AccessDeniedException("Only treasurer can disburse loans");

        BigDecimal amount = loan.getAmount();
        Long groupId = loan.getMember().getGroup().getId();
        groupFundService.subtractFromFund(groupId, amount);

        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        loan.setRemainingBalance(amount);

        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(
                LoanAuditLog.builder()
                        .loan(updated)
                        .status(LoanStatus.DISBURSED)
                        .performedBy(treasurer.getUsername())
                        .timestamp(LocalDateTime.now())
                        .description("Disbursed ₹" + amount + " by " + treasurer.getUsername())
                        .build()
        );

        return toDto(updated);
    }

    @Transactional
    public LoanDto rejectLoan(Long loanId, MemberUser user) {
        Loan loan = getLoanOrThrow(loanId);

        if (!List.of(LoanStatus.PENDING, LoanStatus.APPROVED, LoanStatus.DISBURSE_REQUESTED)
                .contains(loan.getStatus()))
            throw new IllegalStateException("Loan cannot be rejected at this stage");

        loan.setStatus(LoanStatus.REJECTED);

        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(
                LoanAuditLog.builder()
                        .loan(updated)
                        .status(LoanStatus.REJECTED)
                        .performedBy(user.getUsername())
                        .timestamp(LocalDateTime.now())
                        .description("Rejected by " + user.getUsername())
                        .build()
        );

        return toDto(updated);
    }

    @Transactional
    public LoanDto cancelLoan(Long loanId, MemberUser memberUser) {
        Loan loan = getLoanOrThrow(loanId);

        if (!loan.getMember().getId().equals(memberUser.getMember().getId()))
            throw new AccessDeniedException("You can only cancel your own loan");

        if (!List.of(LoanStatus.PENDING, LoanStatus.APPROVED, LoanStatus.DISBURSE_REQUESTED).contains(loan.getStatus()))
            throw new IllegalStateException("Loan cannot be cancelled in its current status");

        loan.setStatus(LoanStatus.CANCELLED);
        Loan updated = loanRepo.save(loan);

        auditLogRepo.save(
                LoanAuditLog.builder()
                        .loan(updated)
                        .status(LoanStatus.CANCELLED)
                        .performedBy(memberUser.getUsername())
                        .timestamp(LocalDateTime.now())
                        .description("Loan cancelled by member " + memberUser.getUsername())
                        .build()
        );

        return toDto(updated);
    }
    @Transactional
    public LoanDto repayAndUpdateFund(Long loanId, BigDecimal repaymentAmount, MemberUser memberUser) {
        Loan loan = getLoanOrThrow(loanId);

        if (!loan.getMember().getId().equals(memberUser.getMember().getId()))
            throw new AccessDeniedException("Unauthorized loan repayment attempt");

        if (loan.getStatus() != LoanStatus.DISBURSED && loan.getStatus() != LoanStatus.REPAID)
            throw new IllegalStateException("Only DISBURSED loans can be repaid");

        BigDecimal newBalance = loan.getRemainingBalance().subtract(repaymentAmount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Repayment exceeds remaining balance");

        loan.setRemainingBalance(newBalance);
        loan.setEmiPaidCount((loan.getEmiPaidCount() == null ? 1 : loan.getEmiPaidCount() + 1));

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.REPAID);
            loan.setRepaymentDate(LocalDate.now());
        }

        Loan updated = loanRepo.save(loan);

        groupFundService.addToFund(loan.getMember().getGroup().getId(), repaymentAmount);

        auditLogRepo.save(
                LoanAuditLog.builder()
                        .loan(updated)
                        .status(loan.getStatus())
                        .performedBy(memberUser.getUsername())
                        .timestamp(LocalDateTime.now())
                        .description("Repayment of ₹" + repaymentAmount + " by " + memberUser.getUsername())
                        .build()
        );

        return toDto(updated);
    }

    public List<LoanDto> getLoansByMember(Member member) {
        return loanRepo.findByIdWithMember(member.getId()).stream().map(this::toDto).toList();
    }

    public List<LoanDto> getLoansByGroupId(Long groupId) {
        return loanRepo.findByMember_Group_Id(groupId).stream().map(this::toDto).toList();
    }

    public List<LoanAuditLogDto> getAuditLogs(Long loanId) {
        return auditLogRepo.findByLoan_IdOrderByTimestampAsc(loanId).stream()
                .map(this::toLogDto).toList();
    }

    public List<LoanDto> getMonthlyRepayments(Long groupId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return loanRepo.findByMember_Group_IdAndRepaymentDateBetween(groupId, start, end)
                .stream().map(this::toDto).toList();
    }

    public List<LoanDto> getOverdueLoans(Long groupId) {
        LocalDate cutoff = LocalDate.now().minusDays(90);
        return loanRepo.findByMember_Group_IdAndStatusAndDisbursementDateBefore(
                groupId, LoanStatus.DISBURSED, cutoff).stream().map(this::toDto).toList();
    }

    public List<Member> getMembersWithOverdueLoans(Long groupId) {
        return getOverdueLoans(groupId).stream()
                .map(dto -> loanRepo.findById(dto.getId()).orElse(null))
                .filter(Objects::nonNull)
                .map(Loan::getMember)
                .distinct()
                .toList();
    }

    // --- Helper methods ---
    private Loan getLoanOrThrow(Long id) {
        return loanRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    }

    private LoanDto toDto(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .remainingBalance(loan.getRemainingBalance())
                .status(loan.getStatus().name())
                .purpose(loan.getPurpose())
                .purposeDescription(loan.getPurposeDescription())
                .applicationDate(loan.getApplicationDate())
                .approvalDate(loan.getApprovalDate())
                .disbursementDate(loan.getDisbursementDate())
                .repaymentDate(loan.getRepaymentDate())
                .monthlyIncome(loan.getMonthlyIncome())
                .monthlyEmi(loan.getMonthlyEmi())
                .tenureMonths(loan.getTenureMonths())
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