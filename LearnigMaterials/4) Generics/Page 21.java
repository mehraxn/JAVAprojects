public static <T extends Comparable<T>>
void sort(T[] v) {
    for (int i = 1; i < v.length; i++) {
        for (int j = 1; j < v.length; j++) {
            if (v[j - 1].compareTo(v[j]) > 0) {
                T temp = v[j];
                v[j] = v[j - 1];
                v[j - 1] = temp;
            }
        }
    }
}
