package src.backendServer.bankDetails;

import src.constants.Currency;

public class Account{
    private int accNumber;
    private String accHolderName;
    private String accPassword;
    private Currency accCurr;
    private float accBalance;

    //Constructor
    public Account() {}
    public Account(int accNumber, String accHolderName, String accPassword, Currency accCurr, float accBalance)
    {
        this.accNumber = accNumber;
        this.accHolderName = accHolderName;
        this.accPassword = accPassword;
        this.accCurr = accCurr;
        this.accBalance = accBalance;
    }

    public int getAccNumber() {
        return accNumber;
    }
    public void setAccNumber(int accNumber){
        this.accNumber = accNumber;
    }
    public String getAccHolderName(){
        return accHolderName;
    }
    public void setAccHolderName(String accHolderName){
        this.accHolderName = accHolderName;
    }
    public String getAccPassword(){
        return accPassword;
    }
    public void setAccPassword(String accPassword){
        this.accPassword = accPassword;
    }
    public Currency getAccCurr(){
        return accCurr;
    }
    public void setAccCurr(Currency accCurr){
        this.accCurr = accCurr;
    }
    public float getAccBalance(){
        return accBalance;
    }
    public void setAccBalance(float accBalance){
        this.accBalance = accBalance;
    }

    public static class AccountBuilder{
        private Account account;

        public AccountBuilder(){
            account = new Account();
        }

        public AccountBuilder createAccount(int accNumber, String accHolderName, Currency accCurr, String accPassword, float accBalance){
            account.setAccNumber(accNumber);
            account.setAccHolderName(accHolderName);
            account.setAccPassword(accPassword);
            account.setAccCurr(accCurr);
            account.setAccBalance(accBalance);
            return this;
        }
        public Account build(){
            return account;
        }
    }
}