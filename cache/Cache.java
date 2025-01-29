package aed.cache;

import es.upm.aedlib.Entry;
import es.upm.aedlib.Position;
import es.upm.aedlib.map.*;
import es.upm.aedlib.positionlist.*;


public class Cache<Key,Value> {
  

  // Tamano de la cache
  private int maxCacheSize;

  // NO MODIFICA ESTOS ATTRIBUTOS, NI CAMBIA SUS NOMBRES: mainMemory, cacheContents, keyListLRU

  // Para acceder a la memoria M
  private Storage<Key,Value> mainMemory;
  // Un 'map' que asocia una clave con un ``CacheCell''
  private Map<Key,CacheCell<Key,Value>> cacheContents;
  // Una PositionList que guarda las claves en orden de
  // uso -- la clave mas recientemente usado sera el keyListLRU.first()
  private PositionList<Key> keyListLRU;
  


  // Constructor de la cache. Especifica el tamano maximo 
  // y la memoria que se va a utilizar
  public Cache(int maxCacheSize, Storage<Key,Value> mainMemory) {
    this.maxCacheSize = maxCacheSize;

    // NO CAMBIA
    this.mainMemory = mainMemory;
    this.cacheContents = new HashTableMap<Key,CacheCell<Key,Value>>();
    this.keyListLRU = new NodePositionList<Key>();
  }
  


  // Devuelve el valor que corresponde a una clave "Key"
  public Value get(Key key) {
	// Check if the key is in the cache
      if (cacheContents.containsKey(key)) {
          CacheCell<Key, Value> cell = cacheContents.get(key);
          // Move the accessed key to the front of the LRU list
          keyListLRU.remove(cell.getPos());
          keyListLRU.addFirst(key);
          cell.setPos(keyListLRU.first());
          // Return the value
          return cell.getValue();
      } else {
          // If the key is not in the cache, load it from main memory
          Value value = mainMemory.read(key);
          if (value != null) {
              // Add it to the cache
              put(key, value);
          }
          return value;
      }
  }


	    // Add or update a key-value pair in the cache
	    public void put(Key key, Value value) {
	        if (cacheContents.containsKey(key)) {
	            // If the key exists, update its value and mark as dirty
	            CacheCell<Key, Value> cell = cacheContents.get(key);
	            cell.setValue(value);
	            cell.setDirty(true); // Mark it as modified
	            updateLRU(cell, key); // Update LRU
	        } else {
	            // Cache is full, we need to evict the least recently used item
	            if (cacheContents.size() == maxCacheSize) {
	                evictLRU();
	            }
	            // Add new entry to cache
	            keyListLRU.addFirst(key); // Add key to front of LRU list
	            Position<Key> pos = keyListLRU.first(); // Retrieve the position of the newly added key
	            CacheCell<Key, Value> newCell = new CacheCell<>(value, true, pos); // Mark as dirty because it's new
	            cacheContents.put(key, newCell); // Add to cache
	        }
	    }

	    // Evict the least recently used (LRU) entry from the cache
	    private void evictLRU() {
	        Position<Key> lastPos = keyListLRU.last(); // Get the position of the least recently used key
	        Key lruKey = lastPos.element(); // Get the actual key from the position
	        CacheCell<Key, Value> lruCell = cacheContents.get(lruKey);

	        if (lruCell.getDirty()) {
	            // If the evicted cell is dirty, write it back to main memory
	            mainMemory.write(lruKey, lruCell.getValue());
	        }

	        // Remove the least recently used key from both the cache and the LRU list
	        cacheContents.remove(lruKey);
	        keyListLRU.remove(lastPos); // Remove the position from the LRU list
	    }

	    // Update the LRU list to reflect that a key was just accessed
	    private void updateLRU(CacheCell<Key, Value> cell, Key key) {
	        keyListLRU.remove(cell.getPos()); // Remove the current position of the key
	        keyListLRU.addFirst(key); // Add key to front of LRU list
            Position<Key> newPos = keyListLRU.first(); // Retrieve the position of the newly added key
	        cell.setPos(newPos); // Update the position in the cache cell
	    }


  // NO CAMBIA
  public String toString() {
    return "cache";
  }
}


