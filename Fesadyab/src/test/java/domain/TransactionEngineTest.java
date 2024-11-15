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

    @Test
    public void detectFraudulentTransaction_TransactionIsDebitAndHighAmount_ReturnsDifferenceBetweenAmountAndDoubleOfAvgAmount() {
        Transaction transaction = createDebitTransactionWithId(5);
        transaction.amount = 5;
        transaction.accountId = 2;
        int avgAmount = 2;
        when(transactionEngine.getAverageTransactionAmountByAccount(2)).thenReturn(avgAmount);

        int result = transactionEngine.detectFraudulentTransaction(transaction);

        assertThat(result).isOne();
    }

    @Test
    public void detectFraudulentTransaction_TransactionIsNotDebitAndHighAmount_ReturnsZero() {
        Transaction transaction = createTransactionWithId(5);
        transaction.amount = 5000;

        int result = transactionEngine.detectFraudulentTransaction(transaction);

        assertThat(result).isZero();
    }

    @Test
    public void detectFraudulentTransaction_TransactionIsDebitAndLowAmount_ReturnsZero() {
        Transaction transaction = createDebitTransactionWithId(5);

        int result = transactionEngine.detectFraudulentTransaction(transaction);

        assertThat(result).isZero();
    }

    @Test
    public void detectFraudulentTransaction_TransactionIsNotDebitAndLowAmount_ReturnsZero() {
        Transaction transaction = createTransactionWithId(5);

        int result = transactionEngine.detectFraudulentTransaction(transaction);

        assertThat(result).isZero();
    }

    @Test
    public void getTransactionPatternAboveThreshold_TransactionHistoryIsEmpty_ReturnsZero() {
        TransactionEngine emptyEngine = new TransactionEngine();

        int result = emptyEngine.getTransactionPatternAboveThreshold(THRESHOLD);

        assertThat(result).isZero();
    }

    @Test
    public void getTransactionPatternAboveThreshold_AllTransactionAreLessThanThreshold_DiffIsZero() {
        int diff = transactionEngine.getTransactionPatternAboveThreshold(THRESHOLD);

        assertThat(diff).isZero();
    }

    @Test
    public void getTransactionPatternAboveThreshold_OneTransactionIsGreaterThanThreshold_DiffIsEqualToDifferenceBetweenTargetTransactionAndFirstTransaction() {
        Transaction targetTransaction = createTransactionWithId(5);
        targetTransaction.amount = 5000;
        transactionEngine.addTransactionAndDetectFraud(targetTransaction);
        int expectedDiff = 4000;

        int diff = transactionEngine.getTransactionPatternAboveThreshold(THRESHOLD);

        assertThat(diff).isEqualTo(expectedDiff);
    }

    @Test
    public void getTransactionPatternAboveThreshold_MoreThanOneTransactionIsGreaterThanThresholdButDifferenceBetweenAllOfThemIsEqual_DiffIsEqualToDifferenceBetweenAllOfThem() {
        Transaction targetTransaction1 = createTransactionWithId(5);
        Transaction targetTransaction2 = createTransactionWithId(6);
        targetTransaction1.amount = 5000;
        targetTransaction2.amount = 9000;
        transactionEngine.addTransactionAndDetectFraud(targetTransaction1);
        transactionEngine.addTransactionAndDetectFraud(targetTransaction2);
        int expectedDiff = 4000;

        int diff = transactionEngine.getTransactionPatternAboveThreshold(THRESHOLD);

        assertThat(diff).isEqualTo(expectedDiff);
    }

    @Test
    public void getTransactionPatternAboveThreshold_MoreThanOneTransactionIsGreaterThanThresholdAndDifferenceBetweenAllOfThemIsNotSame_ReturnsZero() {
        Transaction targetTransaction1 = createTransactionWithId(5);
        Transaction targetTransaction2 = createTransactionWithId(6);
        targetTransaction1.amount = 5000;
        targetTransaction2.amount = 10000;
        transactionEngine.addTransactionAndDetectFraud(targetTransaction1);
        transactionEngine.addTransactionAndDetectFraud(targetTransaction2);

        int diff = transactionEngine.getTransactionPatternAboveThreshold(THRESHOLD);

        assertThat(diff).isZero();
    }
}
