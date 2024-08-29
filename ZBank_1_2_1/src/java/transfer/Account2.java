package transfer;
public class Account2 {
        private int accountId;
        private double balance;
        private String name;
        private String lastName;
        private String address;
        private String email;

        
    
public Account2 ( int accountId, double balance, String name ,String lastName, String address, String email){
    this.accountId=accountId;
    this.balance=balance;
    this.name=name;
    this.lastName=lastName;
    this.address=address;
    this.email=email;
}
public int getAccountId() { return accountId; }
        public void setAccountId(int accountId) { this.accountId = accountId; }

        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
}