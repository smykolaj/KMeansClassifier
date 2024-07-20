import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class KMeans
{
    private final Path file;
    private ArrayList<Cluster> clusters = new ArrayList<>();
    private ArrayList<Vector> vectors = new ArrayList<>();
    int k;

    public KMeans(int k, String data)
    {
        this.k = k;
        this.file = Path.of(data);
        setVectors();
        setInitialCentroids();
        train();
        printResults();
    }

    private void printResults()
    {
        int i = 1;
        for (Cluster c : clusters){

            HashMap<String , Integer> map = new HashMap<>();
            for (Vector v : c.vectorsInCluster)
                map.put(v.name, map.getOrDefault(v.name, 0)+1);
            System.out.print("Cluster" + i +". Result: ");
            for (String s : map.keySet())
                System.out.println(s + ": " + map.get(s));
            i++;
        }
    }

    private void setVectors()
    {
        try
        {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines)
            {
                vectors.add(new Vector(line));
            }
        } catch (IOException e)
        {
            System.out.println("File not found");
        }
    }

    private void setInitialCentroids()
    {
        if (k < 1)
        {
            System.out.println("Wrong number of k chosen!");
            return;
        }
        for (int i = 0; i < k; i++)
        {
            Cluster cluster = new Cluster();
            cluster.centroid_values = vectors.get((int) (Math.random() * vectors.size())).values;
            clusters.add(cluster);
        }
    }


    private double calcDistance(Vector v, Cluster c)
    {
        double distance = 0.0;
        for (int i = 0; i < v.getDimension(); i++)
        {
            distance += Math.pow(v.values.get(i) - c.centroid_values.get(i), 2);
        }
        return Math.sqrt(distance);
    }

    private double calcDistanceForAll()
    {
        double distance = 0.0;
        for (Cluster c : clusters)
        {
            for (Vector v : c.vectorsInCluster )
                distance += calcDistance(v, c);
        }
        return distance;
    }

    private void recalculateAllClusters()
    {
        for (Cluster c : clusters)
        {
            if (c.vectorsInCluster.isEmpty())
            {
                continue;
            }
            int len = c.vectorsInCluster.size();
            ArrayList<Double> sum = new ArrayList<>(Collections.nCopies(c.centroid_values.size(), 0.0));
            for (int i = 0; i < c.centroid_values.size(); i++ ){
                for (Vector v : c.vectorsInCluster ){
                    sum.set(i, sum.get(i) + v.values.get(i));
                }
                sum.set(i, sum.get(i)/(double)len);
            }
            c.centroid_values = sum;


        }
    }

    private int doIteration()
    {
        int changed = 0;

        for (Cluster c : clusters){
            c.vectorsInCluster.clear();
        }

        for (Vector v : vectors)
        {
            if(findClosestCluster(v))
                changed++;
        }
        recalculateAllClusters();
        return changed;
    }

    private boolean findClosestCluster(Vector v)
    {
        double shortestDistance = Double.MAX_VALUE;
        Cluster c = null;
        boolean changed = false;


        for (Cluster cluster : clusters){
            if (calcDistance(v, cluster) < shortestDistance)
            {
                c = cluster;
                shortestDistance = calcDistance(v, cluster);

            }
        }
        if (v.cluster==null || !v.cluster.equals(c))
            changed = true;
        v.cluster = c;
        c.vectorsInCluster.add(v);
        return changed;

    }

    private void train()
    {
        int it = 0;
        while (true)
        {
            int changes = doIteration();
            System.out.println("Iteration " + it + " Total Distance: " + calcDistanceForAll() + " Changes: " + changes);
            it++;
            if (changes == 0)
            {
                changes = doIteration();
                System.out.println("Iteration " + it + " Total Distance: " + calcDistanceForAll() + " Changes: " + changes);
                it++;
                if (changes == 0 )
                    break;
            }
        }
    }
}