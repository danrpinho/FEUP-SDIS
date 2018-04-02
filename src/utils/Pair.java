package utils;

public class Pair<K, V> {
	
	protected K key;
	protected V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) return false;
		Pair obj = (Pair) o;
		return this.key.equals(obj.getKey()) && this.value.equals(obj.getValue());
	}

}
