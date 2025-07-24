package com.db.dsg.data.seeder;

import com.db.dsg.model.Group;
import com.db.dsg.model.GroupFund;
import com.db.dsg.repository.GroupFundRepository;
import com.db.dsg.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GroupFundSeeder {

    private final GroupRepository groupRepo;
    private final GroupFundRepository groupFundRepo;

    public void seedFundForGroup(String groupName, BigDecimal initialBalance) {
        Group group = groupRepo.findByNameIgnoreCase(groupName)
                .orElseThrow(() -> new RuntimeException("Group '" + groupName + "' not found"));

        Optional<GroupFund> existingFundOpt = groupFundRepo.findByGroup_Id(group.getId());

        if (existingFundOpt.isEmpty()) {
            GroupFund fund = new GroupFund();
            fund.setGroup(group);
            fund.setBalance(initialBalance);
            fund.setProfit(BigDecimal.ZERO);
            fund.setLoss(BigDecimal.ZERO);
            fund.setLastUpdated(LocalDate.now());

            groupFundRepo.save(fund);
            System.out.println("✅ GroupFund created for '" + groupName + "' (ID: " + group.getId() + ")");
        } else {
            System.out.println("ℹ️ GroupFund already exists for '" + groupName + "' (ID: " + group.getId() + ")");

            // Optional: Update existing fund if needed
        /*
        GroupFund fund = existingFundOpt.get();
        fund.setBalance(initialBalance); // or fund.getBalance().add(...)
        fund.setLastUpdated(LocalDate.now());
        groupFundRepo.save(fund);
        System.out.println("🔁 GroupFund updated for '" + groupName + "'");
        */
        }
    }
}
