package com.db.dsg.listener;

import com.db.dsg.event.DepositCreatedEvent;
import com.db.dsg.event.LoanCreatedEvent;
import com.db.dsg.model.*;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.SDGImpactRepository;
import com.db.dsg.repository.SDGMappingRepository;
import com.db.dsg.repository.SDGProcessingStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class SDGImpactListener {

    private final SDGMappingRepository mappingRepo;
    private final SDGImpactRepository impactRepo;
    private final SDGProcessingStatusRepository statusRepo;
    private final GroupRepository groupRepo;

    @EventListener
    @Transactional
    public void handleLoanCreated(LoanCreatedEvent event) {

        String purpose = event.getPurpose().toLowerCase();

        mappingRepo.findByKeywordAndActionType(purpose, "LOAN_PURPOSE")
                .ifPresent(mapping -> {
                    Group group = groupRepo.findById(event.getGroupId())
                            .orElseThrow(() -> new RuntimeException("Group not found for loan event"));

                    SDGImpact impact = SDGImpact.builder()
                            .goal(mapping.getGoal())
                            .description(mapping.getDescription())
                            .group(group)
                            .referenceId(event.getLoanId())
                            .referenceType(SDGProcessingStatus.EntityType.LOAN)
                            .impactDate(event.getApplicationDate())
                            .period(event.getApplicationDate().withDayOfMonth(1).toString())
                            .jobsCreated(purpose.contains("business") ? 1 : 0)
                            .womenEmpowered("FEMALE".equalsIgnoreCase(event.getGender()) ? 1 : 0)
                            .savingsGrowth(BigDecimal.ZERO)
                            .build();

                    impactRepo.save(impact);
                    statusRepo.save(new SDGProcessingStatus(
                            null, event.getLoanId(),
                            SDGProcessingStatus.EntityType.LOAN, true, LocalDateTime.now())
                    );

                    log.info("✅ SDG Impact recorded for loan id {}", event.getLoanId());
                });
    }

    @EventListener
    @Transactional
    public void handleDepositCreated(DepositCreatedEvent event) {
        SavingDeposit deposit = event.getDeposit();
        String depositType = deposit.getRemarks().toLowerCase();

        mappingRepo.findByKeywordAndActionType(depositType, "DEPOSIT_TYPE")
                .ifPresent(mapping -> {
                    Group group = deposit.getMember().getGroup();

                    SDGImpact impact = SDGImpact.builder()
                            .goal(mapping.getGoal())
                            .description(mapping.getDescription())
                            .group(group)
                            .referenceId(deposit.getId())
                            .referenceType(SDGProcessingStatus.EntityType.DEPOSIT)
                            .impactDate(deposit.getDate().toLocalDate())
                            .period(deposit.getDate().toLocalDate().withDayOfMonth(1).toString())
                            .jobsCreated(0)
                            .womenEmpowered(deposit.getMember().getGender() == Member.Gender.FEMALE ? 1 : 0)
                            .savingsGrowth(deposit.getAmount())
                            .build();

                    impactRepo.save(impact);
                    statusRepo.save(new SDGProcessingStatus(
                            null, deposit.getId(),
                            SDGProcessingStatus.EntityType.DEPOSIT, true, LocalDateTime.now())
                    );

                    log.info("✅ SDG Impact recorded for deposit id {}", deposit.getId());
                });
    }
}