package entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Balance {
	private int portfolioId;
	private int securityId;
	private double quantity;
	private double value;
	private Timestamp time;
}
