BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        job_name             => 'scheduled_del_unverified',
        job_type             => 'PLSQL_BLOCK',
        job_action           => 'BEGIN DELETE FROM CUSTOMER WHERE is_active = 0 AND created_at + 1 from dual <= SYSDATE; DELETE FROM FREELANCER WHERE is_active = 0 AND created_at + 1 from dual <= SYSDATE; DELETE FROM STAFF WHERE is_active = 0 AND created_at + 1 from dual <= SYSDATE; END;',
        start_date           => SYSDATE,
        repeat_interval      => 'FREQ=DAILY',
        enabled              => TRUE,
        comments             => 'Scheduler tasked with deleting unverified accounts from user tables after a 24h interval.'
    );
END;
/