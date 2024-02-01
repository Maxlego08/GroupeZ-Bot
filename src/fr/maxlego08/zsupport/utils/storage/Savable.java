package fr.maxlego08.zsupport.utils.storage;

public interface Savable {
	
	void save(Persist persist);
	void load(Persist persist);
}
