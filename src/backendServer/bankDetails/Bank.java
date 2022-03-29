package src.backendServer.bankDetails;

import java.io.IOException;
import java.util.*;

import src.backendServer.Callback.Callback;
import src.constants.Currency;

public class Bank {
    private HashMap<Integer, Account> allAccounts;
    private Vector<Callback> allCallbacks;

    public Bank() {
        allAccounts = new HashMap<>();
        allCallbacks = new Vector<>();
    }

    public int createAccount(String accHolderName, String accPassword, Currency accCurr, float accBalance) {
        // Generate random accountNumber using UUID
        int accNum;
        do {
            accNum = new Random().nextInt(900000) + 100000;
        } while (allAccounts.get(accNum) != null);

        Account newAcc = new Account.AccountBuilder()
                .createAccount(accNum, accHolderName, accCurr, accPassword, accBalance)
                .build();

        allAccounts.put(accNum, newAcc);
        System.out.println("Account created, your account number is: " + accNum);
        triggerCallback(String.format("Account %d created", accNum));

        return accNum;
    }

    // Check balance for account
    public float checkBalance(int accNum, String accPassword) {
        float balance = 0;
        Account acc = allAccounts.get(accNum);
        // Check if account exists inside the bank
        if (acc != null) {
            // Check if inputted password is correct
            if (acc.getAccPassword().equals(accPassword)) {
                balance = acc.getAccBalance();
            } else
                balance = -2; // if the return value is -2, its an incorrect pin
        } else {
            balance = -1; // if return value is -1, that means account doesnt exist
        }
        return balance;
    }

    public float convertToUSD(Currency accountCurrency, float amount) {
        HashMap<Currency, Float> exchangeRate = new HashMap<>();
        exchangeRate.put(Currency.SGD, Float.parseFloat("0.74"));
        exchangeRate.put(Currency.MYR, Float.parseFloat("0.24"));
        exchangeRate.put(Currency.JPY, Float.parseFloat("0.0081"));
        exchangeRate.put(Currency.KRW, Float.parseFloat("0.00082"));
        exchangeRate.put(Currency.USD, Float.parseFloat("1"));
        return exchangeRate.get(accountCurrency)*amount;
    }
    public float convertFromUSD(Currency targetCurrency, float amount) {
        HashMap<Currency, Float> exchangeRate = new HashMap<>();
        exchangeRate.put(Currency.SGD, Float.parseFloat("1.35"));
        exchangeRate.put(Currency.MYR, Float.parseFloat("4.17"));
        exchangeRate.put(Currency.JPY, Float.parseFloat("123.46"));
        exchangeRate.put(Currency.KRW, Float.parseFloat("1219.51"));
        exchangeRate.put(Currency.USD, Float.parseFloat("1"));
        return exchangeRate.get(targetCurrency)*amount;
    }

    public float updateBalance(String accHolderName, int accNum, String accPassword, int currencyChoice, int choice, float amount){
        // sgd to USD = 0.74
        // sgd to JPY = 90.76
        // sgd to MYR = 3.10
        // sgd to KRW = 895.41

        System.out.println("Your choice is: " + choice);

        //If account doesnt exit
        if(allAccounts.get(accNum) == null)
            return -1;
        //If pin entered was incorrect
        if(!allAccounts.get(accNum).getAccPassword().equals(accPassword))
            return -2;
        HashMap<Integer, Currency> hashMap = new HashMap<>();
        hashMap.put(1, Currency.SGD);
        hashMap.put(2, Currency.USD);
        hashMap.put(3, Currency.MYR);
        hashMap.put(4, Currency.JPY);
        hashMap.put(5, Currency.KRW);
        Currency targetCurrency = hashMap.get(currencyChoice);
        Currency accountCurrency = allAccounts.get(accNum).getAccCurr();

        if (accountCurrency != targetCurrency) {
            float usdValue = convertToUSD(targetCurrency, amount);
            float correctAmount = convertFromUSD(accountCurrency, usdValue);
            amount = correctAmount;
        }

        if(choice == 1)
        {
            Account temp = allAccounts.get(accNum);
            temp.setAccBalance(temp.getAccBalance() + amount);
            triggerCallback(String.format("%.2f deposited into %d", amount, accNum));
        } else if (choice == 2){
            Account temp = allAccounts.get(accNum);

            if(temp.getAccBalance() > amount)
            {
                temp.setAccBalance(temp.getAccBalance() - amount);
                System.out.println("You have withdrawn " + amount + "." + " The remaining balance in your account is " + temp.getAccBalance());
                triggerCallback(String.format("%.2f withdrawn from %d", amount, accNum));
            }
            else {
                System.out.printf("Account balance is insufficient: ", Float.toString(temp.getAccBalance()));
                return -3;
            }
        }
        else{
            return -4;
        }
        return allAccounts.get(accNum).getAccBalance();
    }

    public int closeAccount(String accHolderName, int accNum, String accPassword){
        Account temp = allAccounts.get(accNum);

        if(temp != null) {
            if(temp.getAccPassword().equals(accPassword))
            {
                allAccounts.remove(accNum);
                System.out.println("Account has been removed");
                triggerCallback(String.format("Account %d removed", accNum));
                return 1;
            }
            // If pin entered was incorrect
            else
                return -2;
        }
        //If account doesn't exist inside the bank
        else
            return -1;
    }

    public void addCallback(String address, Callback callback) {
        allCallbacks.add(callback);
    }

    public void triggerCallback(String message) {
        Iterator<Callback> iterator = allCallbacks.iterator();

        while (iterator.hasNext()) {
            Callback callback = iterator.next();

            if (callback.stillAlive()) {
                try {
                    callback.send(message);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                iterator.remove();
            }
        }
    }

    public int transferFunds(String accHolderName, int accNum, String accPassword, int currencyChoice, float amount, int trfaccountnum) {
        if(allAccounts.get(trfaccountnum) == null) {
            return 1;
        }
        else {
            float trfBalance = updateBalance(accHolderName, accNum, accPassword, currencyChoice,2, amount);
            if (trfBalance == -1) {
                return -1;
            }
            else if (trfBalance == -2) {
                return -2;
            } else if (trfBalance == -3) {
                return -3;
            } else {
                Account trfAccount = allAccounts.get(trfaccountnum);
                Currency accountCurrency = allAccounts.get(trfaccountnum).getAccCurr();
                HashMap<Integer, Currency> trfHashMap = new HashMap<>();
                trfHashMap.put(1, Currency.SGD);
                trfHashMap.put(2, Currency.USD);
                trfHashMap.put(3, Currency.MYR);
                trfHashMap.put(4, Currency.JPY);
                trfHashMap.put(5, Currency.KRW);
                Currency targetCurrency = trfHashMap.get(currencyChoice);

                if (accountCurrency != targetCurrency) {
                    float usdValue = convertToUSD(targetCurrency, amount);
                    float correctAmount = convertFromUSD(accountCurrency, usdValue);
                    amount = correctAmount;
                }
                trfAccount.setAccBalance(trfAccount.getAccBalance() + amount);
                return -4;
            }
        }
    }

    public String getAccountCurrency(int accNum) {
        Currency accountCurrency = allAccounts.get(accNum).getAccCurr();
        return accountCurrency.toString();
    }

}
