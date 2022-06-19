package net.thevpc.kifkif;

import net.thevpc.kifkif.util.AbstractEstimateIterator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSetList implements FileSet {
    private List<FileSet> others = new ArrayList<>();

    public FileSetList(List<FileSet> others) {
        this.others = new ArrayList<>(others);
    }

    @Override
    public EstimateIterator<File> iterate(KifKif kifkif) {
        return new AbstractEstimateIterator<File>() {
            List<EstimateIterator<File>> all = new ArrayList<>();
            List<EstimateIterator<File>> remaining = new ArrayList<>();

            {
                for (FileSet other : others) {
                    all.add(other.iterate(kifkif));
                }
                remaining.addAll(all);
            }

            @Override
            public float progressRatio() {
                if(remaining.isEmpty()){
                    return 1;
                }
                return (float)
                        ((all.size() - remaining.size() + remaining.get(0).progressRatio())*1.0/all.size());
            }

            @Override
            public long consumedCount() {
                long a = 0;
                for (EstimateIterator<File> f : all) {
                    a += f.consumedCount();
                }
                return a;
            }

            @Override
            public long remainingEstimateCount() {
                long a = 0;
                for (EstimateIterator<File> f : remaining) {
                    a += f.remainingEstimateCount();
                }
                return a;
            }

            @Override
            public boolean hasNext() {
                while (true) {
                    if (remaining.size() > 0) {
                        if (remaining.get(0).hasNext()) {
                            return true;
                        } else {
                            remaining.remove(0);
                        }
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public File next() {
                return remaining.get(0).next();
            }
        };
    }

    @Override
    public boolean contains(File file, KifKif kifkif) {
        for (FileSet other : others) {
            if (other.contains(file, kifkif)) {
                return true;
            }
        }
        return false;
    }
}
