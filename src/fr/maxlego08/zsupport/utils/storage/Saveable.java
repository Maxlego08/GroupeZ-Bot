package fr.maxlego08.zsupport.utils.storage;

public interface Saveable {
	
	void save(Persist persist);
	void load(Persist persist);
}
