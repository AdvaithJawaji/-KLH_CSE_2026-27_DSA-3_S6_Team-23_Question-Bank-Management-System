package structures;

/*
 * Structure: CustomArrayList<T>
 * Purpose: A hand-built dynamic array used throughout the project instead of
 * java.util.ArrayList, so the DSA engine and models don't depend on library
 * collections.
 * Time Complexity: get/set O(1), add amortized O(1), remove O(n)
 * Space Complexity: O(n)
 */
@SuppressWarnings("unchecked")
public class CustomArrayList<T> {

    private Object[] data;
    private int size;

    public CustomArrayList() {
        data = new Object[8];
        size = 0;
    }

    public void add(T item) {
        ensureCapacity();
        data[size++] = item;
    }

    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    public void set(int index, T item) {
        checkIndex(index);
        data[index] = item;
    }

    public void removeAt(int index) {
        checkIndex(index);
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
    }

    public boolean contains(T item) {
        for (int i = 0; i < size; i++) {
            if (data[i] == null ? item == null : data[i].equals(item)) return true;
        }
        return false;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    private void ensureCapacity() {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, data.length);
            data = bigger;
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }
}
