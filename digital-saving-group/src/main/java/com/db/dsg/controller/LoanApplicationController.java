package com.db.dsg.controller;

import com.db.dsg.dtos.*;
import com.db.dsg.model.Loan;
import com.db.dsg.model.Member;
import com.db.dsg.model.MemberUser;
import com.db.dsg.service.impl.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<LoanDto> applyLoan(
            @RequestBody @Valid LoanRequestDto request,
            @AuthenticationPrincipal MemberUser memberUser
    ) {
        LoanDto loanDto = loanService.applyLoan(
                memberUser.getMember(),
                request
        );
        return ResponseEntity.ok(loanDto);
    }

    @PostMapping("/{loanId}/approve")
    @PreAuthorize("hasRole('PRESIDENT')")
    public ResponseEntity<LoanDto> approveLoan(
            @PathVariable Long loanId,
            @AuthenticationPrincipal MemberUser user
    ) {
        return ResponseEntity.ok(loanService.approveLoan(loanId, user));
    }

    @PostMapping("/{loanId}/disburse")
    @PreAuthorize("hasRole('TREASURER')")
    public ResponseEntity<LoanDto> disburseLoan(
            @PathVariable Long loanId,
            @AuthenticationPrincipal MemberUser user
    ) {
        return ResponseEntity.ok(loanService.disburseLoan(loanId, user));
    }

    @PostMapping("/{loanId}/reject")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'TREASURER')")
    public ResponseEntity<LoanDto> rejectLoan(
            @PathVariable Long loanId,
            @AuthenticationPrincipal MemberUser user
    ) {
        return ResponseEntity.ok(loanService.rejectLoan(loanId, user));
    }

    @PostMapping("/{loanId}/repay")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<LoanDto> repayLoan(
            @PathVariable Long loanId,
            @RequestBody RepaymentRequestDto request,
            @AuthenticationPrincipal MemberUser memberUser
    ) {
        return ResponseEntity.ok(loanService.repayAndUpdateFund(loanId, request.getAmount(), memberUser));
    }

    @PostMapping("/{loanId}/request-disbursement")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<LoanDto> requestDisbursement(
            @PathVariable Long loanId,
            @AuthenticationPrincipal MemberUser user
    ) {
        return ResponseEntity.ok(loanService.requestDisbursement(loanId, user));
    }

    @PostMapping("/{loanId}/cancel")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<LoanDto> cancelLoan(
            @PathVariable Long loanId,
            @AuthenticationPrincipal MemberUser user
    ) {
        return ResponseEntity.ok(loanService.cancelLoan(loanId, user));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<LoanDto>> getMyLoans(@AuthenticationPrincipal MemberUser user) {
        return ResponseEntity.ok(
                loanService.getLoansByMember(user.getMember()).stream()
                        .toList()
        );
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<LoanDto>> getLoansByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(
                loanService.getLoansByGroupId(groupId).stream()
                        .toList()
        );
    }

    @GetMapping("/{loanId}/logs")
    public ResponseEntity<List<LoanAuditLogDto>> getLoanLogs(@PathVariable Long loanId) {
        List<LoanAuditLogDto> logs = loanService.getAuditLogs(loanId).stream()
                .toList();

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/group/{groupId}/repayments")
    public ResponseEntity<List<LoanDto>> getMonthlyRepayments(
            @PathVariable Long groupId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(
                loanService.getMonthlyRepayments(groupId, YearMonth.of(year, month)).stream()
                        .toList()
        );
    }

    @GetMapping("/group/{groupId}/overdue")
    public ResponseEntity<List<LoanDto>> getOverdueLoans(@PathVariable Long groupId) {
        return ResponseEntity.ok(
                loanService.getOverdueLoans(groupId).stream()
                        .toList()
        );
    }

    @GetMapping("/group/{groupId}/overdue-members")
    public ResponseEntity<List<MemberUserDto>> getOverdueMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(
                loanService.getMembersWithOverdueLoans(groupId).stream()
                        .map(this::mapToMemberDto)
                        .toList()
        );
    }

    // -------- Mapping helpers (replace with ModelMapper if needed) --------

    private LoanDto mapToDto(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .purpose(loan.getPurpose())
                .status(loan.getStatus().name())
                .createdDate(loan.getCreatedDate())
                .memberId(loan.getMember().getId())
                .memberName(loan.getMember().getName())
                .groupId(loan.getMember().getGroup().getId())
                .build();
    }

    private MemberUserDto mapToMemberDto(Member member) {
        return MemberUserDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phone(member.getPhone())
                .groupId(member.getGroup().getId())
                .build();
    }
}
