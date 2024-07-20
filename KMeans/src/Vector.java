import java.util.ArrayList;
import java.util.List;

public class Vector {
    public String name;
    public List<Double> values = new ArrayList<>();
    public Cluster cluster;

    public Vector(String info) {
        String[] splittedInfo = info.split(",");
        for (int i = 0; i < splittedInfo.length - 1; i++)
        {
            values.add(Double.valueOf(splittedInfo[i]));
        }
        name = splittedInfo[splittedInfo.length-1];
    }

    public int getDimension() {
        return values.size();
    }
}
