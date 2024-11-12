package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;


import static domain.DomainTestUtil.*;
import static org.mockito.Mockito.spy;

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
    
}
