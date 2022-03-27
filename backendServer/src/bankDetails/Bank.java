package bankDetails;

import java.util.*;
import Account.*

public class Bank {
    public static HashMap<Integer, Account> allAccounts;
    public Bank(){
        allAccounts = newHashMap<>();
    }

    public static int createAccount(String accHolderName, String accPassword, Currency accCurr, float accBalance){
        //Generate random accountNumber using UUID
        do{
            int accNum =Integer.parseInt(String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 10)));
        }while(allAccounts.get(accNum) != null);

        Account newAcc = new Account.AccountBuilder().createAccount(accNum, accHolderName, accCurr, accPassword, accBalance)
                                                    .build();

        allAccounts.put(accNum, newAcc);
        System.out.println("Account created, your account number is :", accNum);
        return accNum;
    }

    // Check balance for account
    public static float checkBalance(int accNum, String accPassword){
        float balance = 0;
        Account acc = allAccounts.get(accNum);
        //Check if account exists inside the bank
        if(acc != null){
            //Check if inputted password is correct
            if(acc.getAccPassword() == accPassword)
            {
                balance = acc.getAccBalance();
            }
            else
                balance = -2; // if the return value is -2, its an incorrect pin
        }
        else {
            balance = -1; // if return value is -1, that means account doesnt exist
        }
        return balance;
    }

    public static float updateBalance(String accHolderName, int accNum, String accPassword, int choice, float amount){
        System.out.println("Your choice is:", choice);

        //If account doesnt exit
        if(allAccounts.get(accNum) == null)
            return -1;
        //If pin entered was incorrect
        if(allAccounts.get(accNum).getAccountPassword != accPassword)
            return -2;


        if(choice == 1)
        {
            Account temp = allAccounts.get(accNum);
            temp.setAccBalance(temp.getAccBalance() + amount);
        }

        //Withdraw money choice = 1
        if(choice == 2){
            Account temp = allAccounts.get(accNum);

            if(temp.getAccountBalance() > amount)
            {
                temp.setAccBalance(temp.getAccBalance() - amount);
                System.out.println("You have withdrawn " + amount "." + " The remaining balance in your account is " + temp.getAccBalance());
            }
            else {
                System.out.println("Account balance is insufficient: ", Float.toString(temp.getAccBalance()));
                return -3;
            }
        }
        else{
            return -4;
        }
        return allAccounts.get(accNum).getAccBalance();
    }

    public static int closeAccount(String accHolderName, int accNum, String accPassword){
        Account temp = allAccounts.get(accNum);

        if(temp != null) {
            if(temp.getAccountPassword() == accPassword)
            {
                allAccounts.remove(accNum);
                System.out.println("Account has been removed");
                return 1;
            }
            // If pin entered was incorrect
            else
                return -2;
        }
        //If account doesn't exist inside the bank
        else
            return -1
    }
}