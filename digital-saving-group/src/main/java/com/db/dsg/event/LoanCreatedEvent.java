package com.db.dsg.event;

import com.db.dsg.model.Loan;
import com.db.dsg.model.Member;
import com.db.dsg.model.SDGProcessingStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class LoanCreatedEvent {
    private final Long loanId;
    private final String purpose;
    private final String purposeDescription;
    private final LocalDate applicationDate;
    private final SDGProcessingStatus.EntityType referenceType = SDGProcessingStatus.EntityType.LOAN;
    private final Long groupId;
    private final String gender;
    private final String memberName;
    private final String groupName;

    public LoanCreatedEvent(Long loanId, String purpose, String purposeDescription,
                            LocalDate applicationDate, Long groupId, String gender,
                            String memberName, String groupName) {
        this.loanId = loanId;
        this.purpose = purpose;
        this.purposeDescription = purposeDescription;
        this.applicationDate = applicationDate;
        this.groupId = groupId;
        this.gender = gender;
        this.memberName = memberName;
        this.groupName = groupName;
    }
}