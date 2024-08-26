package transfer;
import java.math.BigDecimal;
import java.sql.Timestamp;
public class Transaction {
    private int transactionId;
    private Timestamp transactionDate;
    private String transactionType;
    private String account_id;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceAfterTransaction;
    private String status;
    private String oppositeAccountId;  
    private String oppositeName;      
    private String oppositeLastName; 
    private String senderName;
    private String senderLastName; 

    public Transaction(int transactionId, Timestamp transactionDate, String transactionType, String account_id, BigDecimal amount, String currency, BigDecimal balanceAfterTransaction, String status, String oppositeAccountId, String oppositeName, String oppositeLastName,String senderName, String senderLastName) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.account_id = account_id;
        this.amount = amount;
        this.currency = currency;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.status = status;
        this.oppositeAccountId = oppositeAccountId;
        this.oppositeName = oppositeName;
        this.oppositeLastName = oppositeLastName;
        this.senderLastName=senderLastName;
        this.senderName=senderName;
    }
    public String getSenderName() { return senderName; } 
    public void setSenderName(String senderName) { this.senderName = senderName; } 

    public String getSenderLastName() { return senderLastName; } 
    public void setSenderLastName(String senderLastName) { this.senderLastName = senderLastName; } 

    public String getOppositeAccountId() {return oppositeAccountId;}
    public void setOppositeAccountId(String oppositeAccountId) {this.oppositeAccountId = oppositeAccountId;}

    public String getOppositeName() {return oppositeName;}
    public void setOppositeName(String oppositeName) {this.oppositeName = oppositeName;}

    public String getOppositeLastName() {return oppositeLastName;}
    public void setOppositeLastName(String oppositeLastName) {this.oppositeLastName = oppositeLastName;}
    
    public int getTransactionId() {return transactionId;}
    public void setTransactionId(int transactionId){this.transactionId=transactionId; }
    
    public Timestamp getTransactionDate() {return transactionDate;}
    public void setTransactionDate(Timestamp transactionDate){this.transactionDate=transactionDate ; }
    
    public String getTransactionType() {return transactionType;}
    public void setTransactionType(String transactionType){this.transactionType=transactionType; }
    
    public String getaccount_id() {return account_id;}
    public void setaccount_id(String account_id){this.account_id=account_id; }
    
    public BigDecimal getAmount() {return amount;}
    public void setAmount(BigDecimal amount){this.amount=amount; }
    
    public String getCurrency() {return currency;}    
    public void setCurrency(String currency){this.currency=currency; }
    
    public BigDecimal getBalanceAfterTransaction() {return balanceAfterTransaction;}    
    public void setBalanceAfterTransaction(BigDecimal balanceAfterTransaction){this.balanceAfterTransaction=balanceAfterTransaction; }
    
    public String getStatus() {return status;}    
    public void setStatus(String status){this.status=status; }
}



