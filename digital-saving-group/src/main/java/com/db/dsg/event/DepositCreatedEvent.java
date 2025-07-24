package com.db.dsg.event;

import com.db.dsg.model.SavingDeposit;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DepositCreatedEvent extends ApplicationEvent {

    private final SavingDeposit deposit;

    public DepositCreatedEvent(Object source, SavingDeposit deposit) {
        super(source);
        this.deposit = deposit;
    }
}