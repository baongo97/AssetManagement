package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Security {
	private String name;
	private String symbol;
	private SecurityCategory category;
	private Currency currency;
	
	public String getCategoryString() {
		return category.name();
	}
	public String getCurrencyString() {
		return currency.name();
	}
}
