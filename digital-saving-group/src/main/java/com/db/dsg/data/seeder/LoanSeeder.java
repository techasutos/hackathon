package com.db.dsg.data.seeder;

import com.db.dsg.dtos.LoanDto;
import com.db.dsg.dtos.LoanRequestDto;
import com.db.dsg.model.Loan;
import com.db.dsg.model.LoanStatus;
import com.db.dsg.model.MemberUser;
import com.db.dsg.repository.LoanApplicationRepository;
import com.db.dsg.repository.MemberUserRepository;
import com.db.dsg.service.impl.LoanApplicationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanSeeder {

    private final LoanApplicationService loanService;
    private final MemberUserRepository userRepo;
    private final LoanApplicationRepository loanRepo;


    @Transactional
    public void seedLoans() {
        log.info("📦 Seeding loans for all status types...");

        MemberUser president = getUserByRole("president1", "PRESIDENT");
        MemberUser treasurer = getUserByRole("treasurer1", "TREASURER");

        List<String> members = List.of("member1", "member2", "member3");

        for (String username : members) {
            MemberUser memberUser = getUserByRole(username, "MEMBER");

            // PENDING
            createLoan(
                    memberUser,
                    "Medical Emergency",
                    "Urgent surgery for family member",
                    BigDecimal.valueOf(1000),
                    6,
                    BigDecimal.valueOf(12000)
            );

            // APPROVED
            var approved = createLoan(
                    memberUser,
                    "Home Repairs",
                    "Fixing roof leakage",
                    BigDecimal.valueOf(1200),
                    6,
                    BigDecimal.valueOf(12000)
            );
            loanService.approveLoan(approved.getId(), president);

            // REJECTED
            var rejected = createLoan(
                    memberUser,
                    "Agricultural Equipment",
                    "Buying electric water pump",
                    BigDecimal.valueOf(1100),
                    8,
                    BigDecimal.valueOf(15000)
            );
            loanService.rejectLoan(rejected.getId(), president);

            // DISBURSED
            var disbursed = createLoan(
                    memberUser,
                    "Education Fee",
                    "College fee for daughter",
                    BigDecimal.valueOf(2000),
                    10,
                    BigDecimal.valueOf(18000)
            );
            loanService.approveLoan(disbursed.getId(), president);
            loanService.requestDisbursement(disbursed.getId(), memberUser);
            loanService.disburseLoan(disbursed.getId(), treasurer);
            updateDueDate(disbursed.getId(), LocalDate.now().plusDays(20));

            // OVERDUE
            var overdue = createLoan(
                    memberUser,
                    "Small Business Setup",
                    "Open tailoring shop",
                    BigDecimal.valueOf(2200),
                    10,
                    BigDecimal.valueOf(18000)
            );
            loanService.approveLoan(overdue.getId(), president);
            loanService.requestDisbursement(overdue.getId(), memberUser);
            loanService.disburseLoan(overdue.getId(), treasurer);
            updateDueDate(overdue.getId(), LocalDate.now().minusDays(30));

            // REPAID
            var repaid = createLoan(
                    memberUser,
                    "Livestock Purchase",
                    "Goats for dairy business",
                    BigDecimal.valueOf(2500),
                    5,
                    BigDecimal.valueOf(15000)
            );
            loanService.approveLoan(repaid.getId(), president);
            loanService.requestDisbursement(repaid.getId(), memberUser);
            loanService.disburseLoan(repaid.getId(), treasurer);
            updateDueDate(repaid.getId(), LocalDate.now().minusDays(60));
            markRepaid(repaid.getId());

            // CANCELLED
            var cancelled = createLoan(
                    memberUser,
                    "Shop Inventory",
                    "Stock new products",
                    BigDecimal.valueOf(1800),
                    6,
                    BigDecimal.valueOf(12000)
            );
            loanService.cancelLoan(cancelled.getId(), memberUser);
        }

        log.info("✅ Loans seeded successfully with all status variations.");
    }

    private MemberUser getUserByRole(String username, String roleName) {
        return userRepo.findByUsername(username)
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase(roleName)))
                .orElseThrow(() -> new RuntimeException("User not found or missing role: " + username));
    }

    private void updateDueDate(Long loanId, LocalDate dueDate) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        loan.setRepaymentDate(dueDate);
        loanRepo.save(loan);
    }

    private void markRepaid(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        loan.setStatus(LoanStatus.REPAID);
        loanRepo.save(loan);
    }

    private LoanDto createLoan(MemberUser memberUser, String purpose, String description,
                               BigDecimal amount, int tenureMonths, BigDecimal monthlyIncome) {
        BigDecimal emi = amount.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        LoanRequestDto request = LoanRequestDto.builder()
                .amount(amount)
                .purpose(purpose)
                .purposeDescription(description)
                .tenureMonths(tenureMonths)
                .monthlyIncome(monthlyIncome)
                .monthlyEmi(emi)
                .build();
        return loanService.applyLoan(memberUser.getMember(), request);
    }
}