module JDT

import Map;
import Resources;
import Java;

public alias JDTlocation = tuple[str fileName, int offset, int length];
public alias BindingRel = rel[JDTlocation, Entity];
public alias EntityRel = rel[Entity, Entity];
public alias EntitySet = set[Entity];

public alias FactMap = map[str, value];

/*
FactMaps contain the following relations:
  BindingRel typeBindings        (loc x type)
  BindingRel methodBindings      (loc x method)
  BindingRel constructorBindings (loc x constructor)
  BindingRel fieldBindings       (loc x field)
  BindingRel variableBindings    (loc x variable) (local variables and method parameters)
  BindingRel packageBindings     (loc x package)
  EntityRel  implements          (class x interface)
  EntityRel  extends             (class x class)
  EntitySet  declaredTopTypes    (type) (top classes)
  EntityRel  declaredSubTypes    (type x type) (innerclasses)
  EntityRel  declaredMethods     (type x method)
  EntityRel  declaredFields      (type x field)
*/


// import JDT facts from file (path relative to project root)
public FactMap java extractFacts(str project, str file)
@javaClass{org.meta_environment.rascal.eclipse.lib.JDT}
;

// import JDT facts from file (absolute file system path)
public FactMap java extractFacts(str project, loc file)
@javaClass{org.meta_environment.rascal.eclipse.lib.JDT}
;

// extract facts from a single project
public FactMap extractFacts(str projectName) {
	FactMap result = ();
	FactMap temp = ();
	Resource project = getProject(projectName);
	
	for (file(filename, "java", location) <- project) {
	    try {
			temp = extractFacts(projectName, location);
			result = unionFacts(result, temp);
		} catch: ;
	}
	
	return result;
}

// extract facts from projects
public FactMap extractFacts(set[str] projects) {
	FactMap result = ();
	
	for (p <- projects) {
		result = unionFacts(result, extractFacts(p));
	}
	
	return result;
}

// extracts facts from projects and all projects they depends on (transitively)
public FactMap extractFactsTransitive(set[str] projects) {
	FactMap result = ();
	
	for (p <- projects) {
		result = unionFacts(result, extractFacts(p));
		result = unionFacts(result, extractFactsTransitive(references(p)));
	}
	
	return result;
}


// retrieve typed facts from a fact map
public BindingRel getTypeBindings(FactMap fm) { return (BindingRel r := fm["typeBindings"]) ? r : {}; } 
public BindingRel getMethodBindings(FactMap fm) { return (BindingRel r := fm["methodBindings"]) ? r : {}; } 
public BindingRel getConstructorBindings(FactMap fm) { return (BindingRel r := fm["constructorBindings"]) ? r : {}; }
public BindingRel getFieldBindings(FactMap fm) { return (BindingRel r := fm["fieldBindings"]) ? r : {}; }
public BindingRel getVariableBindings (FactMap fm) { return (BindingRel r := fm["variableBindings"]) ? r : {}; }
	// *** JOPPE ADDED START ***
public BindingRel getPackageBindings (FactMap fm) { return (BindingRel r := fm["packageBindings"]) ? r : {}; }
public EntitySet getDeclaredTopTypes (FactMap fm) { return (EntitySet r := fm["declaredTopTypes"]) ? r : {}; }
	// *** JOPPE ADDED END ***
public EntityRel getImplements(FactMap fm) { return (EntityRel r := fm["implements"]) ? r : {}; }
public EntityRel getExtends(FactMap fm) { return (EntityRel r := fm["extends"]) ? r : {}; }
public EntityRel getDeclaredSubTypes(FactMap fm) { return (EntityRel r := fm["declaredSubTypes"]) ? r : {}; }
public EntityRel getDeclaredMethods(FactMap fm) { return (EntityRel r := fm["declaredMethods"]) ? r : {}; }
public EntityRel getDeclaredFields(FactMap fm) { return (EntityRel r := fm["declaredFields"]) ? r : {}; }


// compose two relations by matching JDT locations with Rascal locations
// returns a tuple with the composition result and the locations that could not be matched
public tuple[rel[&T1, &T2] found, rel[JDTlocation, &T2] notfound] matchLocations(rel[&T1, loc] RSClocs, rel[JDTlocation, &T2] JDTlocs) {

  rel[&T1, &T2] found = {};
  BindingRel notfound = {};

  for ( jl <- JDTlocs, <<str url, int offset, int length>, &T2 v2> := jl ) {
    rel[&T1, &T2] search = { <v1, v2> | <&T1 v1, loc l> <- RSClocs,
      l.url == url, l.offset == offset, l.length == length };

    if (search != {}) {
      found += search;
    } else {
      // If a declaration is preceded by Javadoc comments, the JDT parser includes them
      // in the location info of the declaration node. Then the node's location doesn't
      // match with 'ours'. Here we try to find the longest location that ends at the same
      // position as the JDT node, but starts after the JDT node's offset position.
        
      int closest = offset + length;
      &T1 candidate;

      for ( <&T1 v1, loc l> <- RSClocs ) {
        if (l.url == url && l.offset + l.length == offset + length && l.offset > offset) {
          if (l.offset < closest) {
            closest = l.offset;
            candidate = v1;
          }
        }
      }
      
      if (closest != offset + length) {
        found += {<candidate, v2>};        
      } else {
        notfound += jl;
      }
    }
  }

  return <found, notfound>;
}

// union fact maps, union values for facts that appear in both maps (if possible)
// TBD: implement in Java to generalize over value types
public FactMap unionFacts(FactMap m1, FactMap m2) {

	for (s <- domain(m1) & domain(m2)) {
		if (BindingRel br1 := m1[s] && BindingRel br2 := m2[s]) {
			m1[s] = br1 + br2;
		}
		else if (EntityRel ti1 := m1[s] && EntityRel ti2 := m2[s]) {
			m1[s] = ti1 + ti2;
		}
		else if (EntitySet si1 := m1[s] && EntitySet si2 := m2[s]) {
			m1[s] = si1 + si2;
		}
	}
	
	m1 += ( s:m2[s] | s <- domain(m2) - domain(m1) );

	return m1;
}
