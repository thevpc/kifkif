import org.junit.Test;

import java.util.Arrays;

public class TestSearch {
    @Test
    public void testSearch(){
        String[] in=new String[]{"aa/","aaa/aa/","bb/","bbbb/","cc/"};
        int r = Arrays.binarySearch(in, "bbbb/c/d");
        // r = (-(insertion point) - 1)
        // r = -(insertion point) - 1
        // (insertion point) = -r - 1
        int i=-(r + 1);
        System.out.println(i);
    }
}
