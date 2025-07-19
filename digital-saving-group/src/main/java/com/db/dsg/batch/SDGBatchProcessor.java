package com.db.dsg.batch;

import com.db.dsg.model.*;
import com.db.dsg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SDGBatchProcessor {

    private final SavingDepositRepository depositRepo;
    private final LoanApplicationRepository loanRepo;
    private final SDGMappingRepository mappingRepo;
    private final SDGImpactRepository impactRepo;
    private final SDGProcessingStatusRepository statusRepo;

    public ItemReader<SavingDeposit> depositReader() {
        List<Long> unprocessedIds = statusRepo.findByEntityTypeAndProcessedFalse(SDGProcessingStatus.EntityType.DEPOSIT)
                .stream()
                .map(SDGProcessingStatus::getReferenceId)
                .toList();
        return new ListItemReader<>(depositRepo.findAllById(unprocessedIds));
    }

    public ItemProcessor<SavingDeposit, SDGImpact> depositProcessor() {
        List<SDGMapping> mappings = mappingRepo.findByActionType("DEPOSIT_TYPE");

        return deposit -> {
            if (deposit.getRemarks() == null || deposit.getMember() == null) return null;

            Group group = deposit.getMember().getGroup();
            if (group == null) return null;

            for (SDGMapping mapping : mappings) {
                String keyword = mapping.getKeyword();
                if (keyword != null && deposit.getRemarks().toLowerCase().contains(keyword.toLowerCase())) {
                    return buildImpact(
                            group,
                            mapping,
                            deposit.getAmount(),
                            null,
                            null,
                            deposit.getId(),
                            SDGProcessingStatus.EntityType.DEPOSIT
                    );
                }
            }

            return null;
        };
    }

    public ItemReader<Loan> loanReader() {
        List<Long> unprocessedIds = statusRepo.findByEntityTypeAndProcessedFalse(SDGProcessingStatus.EntityType.LOAN)
                .stream()
                .map(SDGProcessingStatus::getReferenceId)
                .toList();
        return new ListItemReader<>(loanRepo.findAllById(unprocessedIds));
    }

    public ItemProcessor<Loan, SDGImpact> loanProcessor() {
        List<SDGMapping> mappings = mappingRepo.findByActionType("LOAN_PURPOSE");

        return loan -> {
            if (loan.getPurpose() == null || loan.getMember() == null) return null;

            for (SDGMapping mapping : mappings) {
                if (loan.getPurpose().toLowerCase().contains(mapping.getKeyword().toLowerCase())) {
                    int jobs = mapping.getGoal().name().contains("DECENT_WORK") ? 1 : 0;

                    int women = 0;
                    if (loan.getMember().getGender() != null &&
                            loan.getMember().getGender().name().equalsIgnoreCase("FEMALE")) {
                        women = 1;
                    }

                    return buildImpact(
                            loan.getMember().getGroup(),
                            mapping,
                            null,
                            jobs,
                            women,
                            loan.getId(),
                            SDGProcessingStatus.EntityType.LOAN
                    );
                }
            }
            return null;
        };
    }

    public ItemWriter<SDGImpact> impactWriter() {
        return impacts -> {
            for (SDGImpact impact : impacts) {
                if (impact == null) continue;

                impactRepo.save(impact);

                SDGProcessingStatus status = statusRepo
                        .findByReferenceIdAndEntityType(impact.getReferenceId(), impact.getReferenceType())
                        .orElse(new SDGProcessingStatus(
                                null,
                                impact.getReferenceId(),
                                impact.getReferenceType(),
                                false,
                                null
                        ));

                status.setProcessed(true);
                status.setProcessedAt(LocalDateTime.now());
                statusRepo.save(status);
            }
        };
    }

    private SDGImpact buildImpact(Group group, SDGMapping mapping,
                                  BigDecimal savingsGrowth, Integer jobs, Integer women,
                                  Long refId, SDGProcessingStatus.EntityType type) {
        return SDGImpact.builder()
                .group(group)
                .goal(mapping.getGoal())
                .description(mapping.getDescription())
                .impactDate(LocalDate.now())
                .period(YearMonth.now().toString())
                .referenceId(refId)
                .referenceType(type)
                .savingsGrowth(savingsGrowth)
                .jobsCreated(jobs)
                .womenEmpowered(women)
                .build();
    }
}
