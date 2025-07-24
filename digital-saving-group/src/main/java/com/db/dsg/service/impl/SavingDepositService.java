package com.db.dsg.service.impl;

import com.db.dsg.dtos.MemberDepositHistoryResponse;
import com.db.dsg.dtos.SavingDepositDto;
import com.db.dsg.dtos.SavingDepositRequest;
import com.db.dsg.dtos.SavingSummaryResponse;
import com.db.dsg.event.DepositCreatedEvent;
import com.db.dsg.model.Member;
import com.db.dsg.model.SavingDeposit;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.repository.SavingDepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingDepositService {

    private final SavingDepositRepository savingDepositRepo;
    private final MemberRepository memberRepo;

    private final ApplicationEventPublisher eventPublisher;

    public SavingDepositDto save(SavingDepositRequest req, Member member) {
        SavingDeposit deposit = new SavingDeposit();
        deposit.setAmount(req.getAmount());
        deposit.setRemarks(req.getRemarks());
        deposit.setMember(member);

        SavingDeposit saved = savingDepositRepo.save(deposit);
        eventPublisher.publishEvent(new DepositCreatedEvent(this, saved));
        return mapToDto(saved);
    }

    public List<SavingDepositDto> getDepositsForGroup(Long groupId) {
        return savingDepositRepo.findByMember_Group_Id(groupId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public SavingSummaryResponse getGroupSavingSummary(Long groupId) {
        List<SavingDeposit> deposits = savingDepositRepo.findByMember_Group_Id(groupId);

        BigDecimal total = deposits.stream()
                .map(SavingDeposit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime lastUpdated = deposits.stream()
                .map(SavingDeposit::getDate)
                .max((d1, d2) -> d1.compareTo(d2))
                .orElse(null);// If no deposits yet

        return new SavingSummaryResponse(total, (long) deposits.size(), lastUpdated);
    }

    public MemberDepositHistoryResponse getMemberDepositHistoryWithName(Long memberId) {
        List<SavingDepositDto> deposits = getMemberDepositHistory(memberId);
        String memberName = getMemberNameById(memberId);
        return new MemberDepositHistoryResponse(memberName, deposits);
    }

    public String getMemberNameById(Long memberId) {
        return memberRepo.findById(memberId)
                .map(Member::getName)
                .orElse("Unknown");
    }

    public List<SavingDepositDto> getMemberDepositHistory(Long memberId) {
        return savingDepositRepo.findByMember_IdOrderByDateDesc(memberId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public SavingSummaryResponse getMemberSavingSummary(Long memberId) {
        List<SavingDeposit> deposits = savingDepositRepo.findByMember_Id(memberId);

        BigDecimal total = deposits.stream()
                .map(SavingDeposit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime lastUpdated = deposits.stream()
                .map(SavingDeposit::getDate)
                .max(Comparator.naturalOrder())
                .orElse(null); // return null if no deposits

        return new SavingSummaryResponse(total, (long) deposits.size(), lastUpdated);
    }

    public List<SavingDepositDto> trackMyDeposits(Member member) {
        return savingDepositRepo.findByMember_IdOrderByDateDesc(member.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SavingDepositDto mapToDto(SavingDeposit deposit) {
        return new SavingDepositDto(
                deposit.getId(),
                deposit.getAmount(),
                deposit.getRemarks(),
                deposit.getDate(),
                deposit.getMember().getName()
        );
    }
}