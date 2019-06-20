package bloomfilter;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class BloomFilter<T> {

    private final Funnel elementFunnel;
    private final int size;
    private List<Integer> seeds;

    private BitSet bitArray;

    public BloomFilter(Funnel elementFunnel, int numOfHashFunctions, int size) {
        this.elementFunnel = elementFunnel;
        this.size = size;
        initialHashFunctionSeeds(numOfHashFunctions);
        reset();
    }

    public void add(T element) {
        for (Integer seed : seeds) {
            int index = getHashCode(seed, element);
            bitArray.set(index);
        }
    }

    public boolean mightContain(T element) {
        for (Integer seed : seeds) {
            int index = getHashCode(seed, element);
            if (!bitArray.get(index)) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        this.bitArray = new BitSet(size);
    }

    public int getHashCode(int seed, T element) {
        return Math.floorMod(Hashing.murmur3_32(seed).hashObject(element, elementFunnel).hashCode(), size);
    }

    private void initialHashFunctionSeeds(int numOfHashFunctions) {
        Random random = new Random();
        seeds = new ArrayList<>();
        for (int i=0; i<numOfHashFunctions; i++) {
            seeds.add(random.nextInt());
        }
    }
}
