package entity;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class PortfolioValue {
    private List<Timestamp> time;
    private List<Double> value;
}
