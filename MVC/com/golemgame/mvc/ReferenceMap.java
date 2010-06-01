package com.golemgame.mvc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A reference map is used to assign unique references well making copies, control the behaviour of the reference assigning,
 * and to provide a mapping between the old references and the new ones.
 * @author Sam
 *
 */
public class ReferenceMap {
	private Map<Reference,Reference> references = new HashMap<Reference,Reference>();
	//this is really a hashset, but we need to be able to access the values more easily...
	private Map<Reference,Reference> newReferences = new HashMap<Reference,Reference>();//this is kept identical to the values of references
	//for constant time access
	
	private Map<Reference,ReferenceType> referenceTypeMap = new HashMap<Reference,ReferenceType>();
	
	
	private boolean renewAllReferences = false;
	
	
	public boolean isRenewAllReferences() {
		return renewAllReferences;
	}
	
	/**
	 * Renew all references in the deep copy, not just those marked under 'references to renew.
	 * @param renewAllReferences
	 */
	public ReferenceMap(boolean renewAllReferences) {
		super();
		this.renewAllReferences = renewAllReferences;
	}
	
	
	/**
	 * Renew only references added to 'references to renew'
	 * @param renewAllReferences
	 */
	public ReferenceMap() {
		this(false);
	}

	/**
	 * Set true if all encountered references should be made unique.
	 * Set false is only references in the 'references to renew' set should be made unique
	 * @param renewAllReferences
	 */
	public void setRenewAllReferences(boolean renewAllReferences) {
		this.renewAllReferences = renewAllReferences;
	}

	/**
	 * A list of old references that, when encoutnered, should be made unique.
	 */
	private Set<Reference> referencesToRenew = new HashSet<Reference>();
	
	public Set<Reference> getReferencesToRenew() {
		return referencesToRenew;
	}



	public Set<Reference> getForbiddenReferences() {
		return forbiddenReferences;
	}


	public void addReferenceToRenew(Reference ref)
	{
		this.referencesToRenew.add(ref);
	}
	
	/**
	 * A complete set of all references that are not allowed for the new set.
	 */
	private Set<Reference> forbiddenReferences = new HashSet<Reference>();
	public Reference createUniqueReference(Reference fromReference, ReferenceType destination)
	{
		//of course... there is a minor loophole: if you later add to the reference map a reference which happens to be the same as
		//a new reference created by this method... then we can go in and fix that here...
		if (! this.renewAllReferences && ! this.referencesToRenew.contains(fromReference))
		{
			return fromReference;
		}
		
		
		while (newReferences.containsKey(fromReference))
		{	
			Reference oldReference = newReferences.remove(fromReference);
			oldReference.makeNew();
			newReferences.put(oldReference, oldReference);
		}
		
		Reference newReference =  references.get(fromReference);
		if (newReference != null)
			return newReference;
		
		newReference = Reference.createUniqueReference();
		while(forbiddenReferences.contains(newReference) || references.keySet().contains(newReference))
			newReference.makeNew();//randomize the value of this reference..
	
		references.put(fromReference, newReference);//don't have to add it to forbidden references.
		newReferences.put(newReference,newReference);
		
		referenceTypeMap.put(newReference, destination);
		
		return newReference;
	}
	
	public ReferenceType getDestination(Reference newReference)
	{
		return referenceTypeMap.get(newReference);
	}
	
	public void addForbiddenReference(Reference reference)
	{
		this.forbiddenReferences.add(reference);
	}
	
	public Reference getNewReference(Reference originalReference)
	{
		return references.get(originalReference);
	}
	
	public Collection<Reference> getOriginalReferences()
	{
		return references.keySet();
	}

}
