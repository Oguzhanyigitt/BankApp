public class Account {
    private int userId;
    private String account_id;
    private double balance;

    public Account(String account_id, double balance,int userId) {
        this.userId=userId;
        this.account_id = account_id;
        this.balance = balance;
    }
   
    
    public int getUserId() {
        return userId;
    }
    public String getaccount_id() {
        return account_id;
    }

    public double getbalance() {
        return balance;
        
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Account{" +
                ", balance=" + balance +
                ", accountId=" + account_id +
                ", userId=" + userId +
                '}';
    }
}

