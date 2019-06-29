package my.mapkn3.foldercleaner.fragment

interface SaveLoadData<K, V> {
    fun saveData(key: K, data: V)

    fun loadData(key: K): V
}