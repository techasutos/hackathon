package com.db.dsg.data.seeder;

import com.db.dsg.dtos.LoanDto;
import com.db.dsg.dtos.LoanRequestDto;
import com.db.dsg.model.Loan;
import com.db.dsg.model.MemberUser;
import com.db.dsg.repository.LoanApplicationRepository;
import com.db.dsg.repository.MemberUserRepository;
import com.db.dsg.service.impl.LoanApplicationService;
import jakarta.annotation.PostConstruct;
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

    public void seedLoans() {
        log.info("📦 Seeding loans for multiple members...");

        MemberUser president = getUserByRole("president1", "PRESIDENT");
        MemberUser treasurer = getUserByRole("treasurer1", "TREASURER");

        List<String> members = List.of("member1", "member2", "member3");

        for (String username : members) {
            MemberUser memberUser = getUserByRole(username, "MEMBER");

            // Loan variations
            createLoan(memberUser, "Pending Loan", BigDecimal.valueOf(1000), 6, BigDecimal.valueOf(12000));

            var approved = createLoan(memberUser, "Approved Loan", BigDecimal.valueOf(1200), 6, BigDecimal.valueOf(12000));
            loanService.approveLoan(approved.getId(), president);

            var rejected = createLoan(memberUser, "Rejected Loan", BigDecimal.valueOf(1100), 8, BigDecimal.valueOf(15000));
            loanService.rejectLoan(rejected.getId(), president);

            var disbursed = createLoan(memberUser, "Disbursed Loan", BigDecimal.valueOf(2000), 10, BigDecimal.valueOf(18000));
            loanService.approveLoan(disbursed.getId(), president);
            loanService.disburseLoan(disbursed.getId(), treasurer);
            updateDueDate(disbursed.getId(), LocalDate.now().plusDays(20));

            var overdue = createLoan(memberUser, "Overdue Loan", BigDecimal.valueOf(2200), 10, BigDecimal.valueOf(18000));
            loanService.approveLoan(overdue.getId(), president);
            loanService.disburseLoan(overdue.getId(), treasurer);
            updateDueDate(overdue.getId(), LocalDate.now().minusDays(30));
        }

        log.info("✅ Loans seeded successfully with due dates and various statuses.");
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

    private LoanDto createLoan(MemberUser memberUser, String purpose, BigDecimal amount, int tenureMonths, BigDecimal monthlyIncome) {
        BigDecimal emi = amount.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        LoanRequestDto request = LoanRequestDto.builder()
                .amount(amount)
                .purpose(purpose)
                .tenureMonths(tenureMonths)
                .monthlyIncome(monthlyIncome)
                .monthlyEmi(emi)
                .build();
        return loanService.applyLoan(memberUser.getMember(), request);
    }
}