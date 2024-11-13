package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;


import static domain.DomainTestUtil.*;
import static org.mockito.Mockito.*;

public class TransactionEngineTest {
    private TransactionEngine transactionEngine;

    @BeforeEach
    public void setup() {
        transactionEngine = spy(TransactionEngine.class);
        transactionEngine.addTransactionAndDetectFraud(createTransactionWithId(1));
        transactionEngine.addTransactionAndDetectFraud(createTransactionWithId(2));
        transactionEngine.addTransactionAndDetectFraud(createDebitTransactionWithId(3));
        transactionEngine.addTransactionAndDetectFraud(createDebitTransactionWithId(4));
    }

    @Test
    public void addTransactionAndDetectFraud_AddRepetitiveTransaction_ReturnsZero() {
        Transaction transaction = createTransactionWithId(1);

        int result = transactionEngine.addTransactionAndDetectFraud(transaction);

        assertThat(result).isZero();
    }

    @Test
    public void addTransactionAndDetectFraud_TransactionIsNotFraudulent_FraudScoreIsEqualToTransactionPattern() {
        Transaction transaction = createTransactionWithId(5);
        int expectedFraudScore = 12345678;
        when(transactionEngine.detectFraudulentTransaction(transaction)).thenReturn(0);
        when(transactionEngine.getTransactionPatternAboveThreshold(anyInt())).thenReturn(expectedFraudScore);

        int fraudScore = transactionEngine.addTransactionAndDetectFraud(transaction);

        assertThat(fraudScore).isEqualTo(expectedFraudScore);
    }

    @Test
    public void addTransactionAndDetectFraud_TransactionIsFraudulent_FraudScoreIsEqualToDetectedFraud() {
        Transaction transaction = createTransactionWithId(5);
        int expectedFraudScore = 123456;
        when(transactionEngine.detectFraudulentTransaction(transaction)).thenReturn(expectedFraudScore);

        int fraudScore = transactionEngine.addTransactionAndDetectFraud(transaction);

        assertThat(fraudScore).isEqualTo(expectedFraudScore);
    }
}
