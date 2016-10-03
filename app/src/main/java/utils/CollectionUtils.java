package utils;

import android.util.LongSparseArray;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {

    public static boolean isEmpty(Collection c){
        return c == null || c.size() == 0;
    }

    public static boolean isNotEmpty(Collection c){
        return !isEmpty(c);
    }

    public static int getItemCount(Collection c){
        return c == null ? 0 : c.size();
    }

    public static <C> List<C> asList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }
}
