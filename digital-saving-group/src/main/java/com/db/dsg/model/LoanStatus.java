package com.db.dsg.model;

public enum LoanStatus {
    PENDING,                // When loan is first applied
    APPROVED,               // Approved by President
    DISBURSE_REQUESTED,     // Member has requested the Treasurer to disburse
    DISBURSED,              // Disbursed by Treasurer
    REPAID,                 // Fully repaid
    REJECTED,               // Rejected at any stage
    CANCELLED              //  If member cancels loans
}
