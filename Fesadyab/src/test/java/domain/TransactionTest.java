package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import static domain.DomainTestUtil.*;
public class TransactionTest {
    private Transaction transaction;

    @BeforeEach
    public void setup() {
        transaction = createTransactionWithId(1);
    }

    @Test
    public void equals_ObjectIsNotInstanceOfTransaction_ReturnsFalse() {
        Object obj = new Object();

        boolean isEqual = transaction.equals(obj);

        assertThat(isEqual).isFalse();
    }

    @Test
    public void equals_TransactionIdsAreNotEqual_ReturnsFalse() {
        Object targetTransaction = createTransactionWithId(2);

        boolean isEqual = transaction.equals(targetTransaction);

        assertThat(isEqual).isFalse();
    }

    @Test
    public void equals_TransactionIdsAreEqual_ReturnsTrue() {
        Object targetTransaction = createTransactionWithId(1);

        boolean isEqual = transaction.equals(targetTransaction);

        assertThat(isEqual).isTrue();
    }
}
