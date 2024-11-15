package domain;

public class DomainTestUtil {
    public static int TRANSACTION_ID = 1;
    public static int ACCOUNT_ID = 1;
    public static int AMOUNT = 1000;
    public static int THRESHOLD = 1000;
    public static Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.transactionId = TRANSACTION_ID;
        transaction.accountId = ACCOUNT_ID;
        transaction.amount = AMOUNT;
        transaction.isDebit = false;
        return transaction;
    }
    public static Transaction createDebitTransaction() {
        Transaction transaction = createTransaction();
        transaction.isDebit = true;
        return transaction;
    }
    public static Transaction createTransactionWithId(int id) {
        Transaction transaction = createTransaction();;
        transaction.transactionId = id;
        return transaction;
    }
    public static Transaction createDebitTransactionWithId(int id) {
        Transaction transaction = createDebitTransaction();
        transaction.transactionId = id;
        return transaction;
    }
}
