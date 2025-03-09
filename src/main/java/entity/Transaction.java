package entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
	private int accountId;
	private int securityId;
	private double quantity;
	private double price;
	private TransactionType type;
	private Timestamp time;
	
	public String getTypeString() {
		return type.name();
	}
	
	public static void main(String[] args) {
		Transaction transaction = new Transaction();
		
		transaction.setType(TransactionType.BUY);
		System.out.println(transaction.getTypeString());
	}
}
