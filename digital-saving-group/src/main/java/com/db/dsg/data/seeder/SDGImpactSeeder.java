package com.db.dsg.data.seeder;

import com.db.dsg.model.*;
import com.db.dsg.repository.LoanApplicationRepository;
import com.db.dsg.repository.SDGImpactRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SDGImpactSeeder {

    private final LoanApplicationRepository loanRepo;
    private final SDGImpactRepository sdgRepo;

    @Transactional
    public void seedSDGImpacts() {
        log.info("🌍 Seeding SDG Impact records from loans...");

        List<Loan> loans = loanRepo.findAll();

        for (Loan loan : loans) {
            Member member = loan.getMember();
            Group group = member.getGroup();
            LocalDate appDate = loan.getApplicationDate();
            String purpose = loan.getPurpose().toLowerCase();

            SDGGoal goal = determineGoal(purpose);
            String desc = loan.getPurposeDescription();

            SDGImpact impact = SDGImpact.builder()
                    .group(group)
                    .goal(goal)
                    .description(desc)
                    .savingsGrowth(BigDecimal.ZERO)
                    .jobsCreated(purpose.contains("business") ? 1 : 0)
                    .womenEmpowered(member.getGender() == Member.Gender.FEMALE ? 1 : 0)
                    .impactDate(appDate)
                    .period(appDate.withDayOfMonth(1).toString().substring(0, 7)) // YYYY-MM
                    .referenceId(loan.getId())
                    .referenceType(SDGProcessingStatus.EntityType.LOAN)
                    .build();

            sdgRepo.save(impact);
        }

        log.info("✅ SDG Impact records seeded successfully.");
    }

    private SDGGoal determineGoal(String purpose) {
        if (purpose.contains("education")) return SDGGoal.QUALITY_EDUCATION;
        if (purpose.contains("business")) return SDGGoal.DECENT_WORK;
        if (purpose.contains("medical") || purpose.contains("health")) return SDGGoal.GOOD_HEALTH;
        if (purpose.contains("agricultur")) return SDGGoal.ZERO_HUNGER;
        if (purpose.contains("women")) return SDGGoal.GENDER_EQUALITY;
        return SDGGoal.NO_POVERTY;
    }
}
