package net.thevpc.kifkif.util;

import net.thevpc.kifkif.DefaultFileSet;

import java.io.File;
import java.io.FileFilter;
import java.util.Stack;

public class FolderVisitorEstimateIterator extends AbstractEstimateIterator<File> {
    private final DefaultFileSet defaultFileSet;
    private final FileFilter compoundFilter;
    long consumed;
    Stack<Data> s;
    Data latest;

    private static class Data {
        File file;
        double from;
        double to;

        public Data(File file, double from, double to) {
            this.file = file;
            this.from = from;
            this.to = to;
        }

        public Data[] split(File[] files) {
            Data[] d = new Data[files.length];
            double delta = (to - from) / d.length;
            for (int i = 0; i < d.length; i++) {
                d[i] = new Data(
                        files[i],
                        from + i * delta,
                        from + (i + 1) * delta
                );
            }
            return d;
        }
    }

    public FolderVisitorEstimateIterator(DefaultFileSet defaultFileSet, FileFilter compoundFilter) {
        this.defaultFileSet = defaultFileSet;
        this.compoundFilter = compoundFilter;
        consumed = 0;
        s = new Stack<Data>();
        s.push(new Data(defaultFileSet.getRoot(), 0, 1));
    }

    @Override
    public long consumedCount() {
        return consumed;
    }

    @Override
    public float progressRatio() {
        if (latest == null) {
            return 0;
        }
        return (float) latest.from;
    }

    @Override
    public long remainingEstimateCount() {
        return s.size();
    }

    public boolean hasNext() {
        return !s.isEmpty();
    }

    public File next() {
        Data y = s.pop();
        Data f = (Data) y;
        if (f.file.isDirectory()) {
            File[] fs = f.file.listFiles(compoundFilter);
            if (fs != null) {
                Data[] n = f.split(fs);
                for (int i = n.length - 1; i >= 0; i--) {
                    s.push(n[i]);
                }
            }
        }
        consumed++;
        latest = f;
        return f.file;
    }

    public void remove() {
        //not implemented
    }
}
